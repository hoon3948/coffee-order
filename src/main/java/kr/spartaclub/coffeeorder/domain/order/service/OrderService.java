package kr.spartaclub.coffeeorder.domain.order.service;

import kr.spartaclub.coffeeorder.global.common.exception.ErrorCode;
import kr.spartaclub.coffeeorder.global.common.exception.MenuNotFoundException;
import kr.spartaclub.coffeeorder.global.common.exception.OrderException;
import kr.spartaclub.coffeeorder.domain.menu.entity.Menu;
import kr.spartaclub.coffeeorder.domain.menu.entity.MenuStatistics;
import kr.spartaclub.coffeeorder.domain.menu.repository.MenuRepository;
import kr.spartaclub.coffeeorder.domain.menu.repository.MenuStatisticsRepository;
import kr.spartaclub.coffeeorder.domain.order.dto.CreateOrderRequest;
import kr.spartaclub.coffeeorder.domain.order.dto.OrderHistoryResponse;
import kr.spartaclub.coffeeorder.domain.order.dto.OrderResponse;
import kr.spartaclub.coffeeorder.domain.order.entity.Order;
import kr.spartaclub.coffeeorder.domain.order.repository.OrderRepository;
import kr.spartaclub.coffeeorder.domain.point.service.UserPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 주문 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final MenuStatisticsRepository menuStatisticsRepository;
    private final UserPointService userPointService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String ORDER_TOPIC = "order-events";

    /**
     * 주문 생성
     * @param userId 사용자 ID
     * @param request 주문 생성 요청
     * @return 주문 응답
     */
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        // 1. 메뉴 조회 및 검증
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new MenuNotFoundException(request.getMenuId()));

        if (!menu.isAvailable()) {
            throw new OrderException(ErrorCode.ORDER_002);
        }

        // 2. 포인트 차감 (비관적 락 사용)
        int remainingBalance;
        try {
            remainingBalance = userPointService.usePoint(
                    userId,
                    menu.getPrice(),
                    "주문: " + menu.getName()
            );
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("포인트 잔액이 부족합니다")) {
                throw new OrderException(ErrorCode.ORDER_003);
            }
            throw e;
        }

        // 3. 주문 생성
        Order order = Order.create(userId, menu.getId(), menu.getPrice());
        Order savedOrder = orderRepository.save(order);

        log.info("주문 생성: orderId={}, userId={}, menuId={}, price={}", 
                savedOrder.getId(), userId, menu.getId(), menu.getPrice());

        // 4. 메뉴 통계 업데이트 (비동기)
        updateMenuStatistics(menu.getId());

        // 5. Kafka 이벤트 발행 (비동기)
        publishOrderEvent(savedOrder, menu.getName());

        return OrderResponse.of(savedOrder, menu.getName(), remainingBalance);
    }

    /**
     * 주문 내역 조회 (최근 30일)
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 주문 내역 목록
     */
    public Page<OrderHistoryResponse> getOrderHistory(Long userId, Pageable pageable) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);

        Page<Order> orders = orderRepository.findByUserIdAndOrderTimeBetween(
                userId, startDate, endDate, pageable
        );

        return orders.map(order -> {
            Menu menu = menuRepository.findByIdIncludingDeleted(order.getMenuId())
                    .orElse(null);
            String menuName = menu != null ? menu.getName() : "삭제된 메뉴";
            return OrderHistoryResponse.of(order, menuName);
        });
    }

    /**
     * 최근 주문 내역 조회
     * @param userId 사용자 ID
     * @param limit 조회 개수
     * @return 주문 내역 목록
     */
    public List<OrderHistoryResponse> getRecentOrders(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Order> orders = orderRepository.findRecentOrdersByUserId(userId, pageable);

        return orders.stream()
                .map(order -> {
                    Menu menu = menuRepository.findByIdIncludingDeleted(order.getMenuId())
                            .orElse(null);
                    String menuName = menu != null ? menu.getName() : "삭제된 메뉴";
                    return OrderHistoryResponse.of(order, menuName);
                })
                .collect(Collectors.toList());
    }

    /**
     * 주문 단건 조회
     * @param orderId 주문 ID
     * @return 주문 정보
     */
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다: " + orderId));
    }

    /**
     * 사용자의 총 주문 횟수 조회
     * @param userId 사용자 ID
     * @return 주문 횟수
     */
    public long getUserOrderCount(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    /**
     * 메뉴 통계 업데이트 (비동기)
     */
    private void updateMenuStatistics(Long menuId) {
        try {
            LocalDate today = LocalDate.now();
            MenuStatistics statistics = menuStatisticsRepository
                    .findByMenuIdAndOrderDate(menuId, today)
                    .orElse(null);

            if (statistics == null) {
                // 오늘 날짜의 통계가 없으면 생성
                statistics = MenuStatistics.createToday(menuId);
                menuStatisticsRepository.save(statistics);
            } else {
                // 기존 통계 업데이트
                statistics.incrementOrderCount();
            }

            log.debug("메뉴 통계 업데이트: menuId={}, date={}, count={}", 
                    menuId, today, statistics.getOrderCount());
        } catch (Exception e) {
            log.error("메뉴 통계 업데이트 실패: menuId={}", menuId, e);
            // 통계 업데이트 실패는 주문 프로세스에 영향을 주지 않음
        }
    }

    /**
     * Kafka 주문 이벤트 발행 (비동기)
     */
    private void publishOrderEvent(Order order, String menuName) {
        try {
            String message = String.format(
                    "{\"orderId\":%d,\"userId\":%d,\"menuId\":%d,\"menuName\":\"%s\",\"price\":%d,\"orderTime\":\"%s\"}",
                    order.getId(),
                    order.getUserId(),
                    order.getMenuId(),
                    menuName,
                    order.getPrice(),
                    order.getOrderTime()
            );

            kafkaTemplate.send(ORDER_TOPIC, order.getId().toString(), message);
            log.debug("Kafka 이벤트 발행: orderId={}", order.getId());
        } catch (Exception e) {
            log.error("Kafka 이벤트 발행 실패: orderId={}", order.getId(), e);
            // Kafka 발행 실패는 주문 프로세스에 영향을 주지 않음
        }
    }
}
