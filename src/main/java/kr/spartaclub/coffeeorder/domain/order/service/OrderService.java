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
import kr.spartaclub.coffeeorder.domain.order.entity.OrderItem;
import kr.spartaclub.coffeeorder.domain.order.repository.OrderItemRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final OrderItemRepository orderItemRepository;
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
        List<CreateOrderRequest.OrderItemRequest> itemRequests = request.getItems();
        List<Long> menuIds = itemRequests.stream()
                .map(CreateOrderRequest.OrderItemRequest::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Menu> menuMap = menuRepository.findAllById(menuIds).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu));

        // 메뉴 존재 여부 및 판매 가능 여부 확인
        List<OrderResponse.OrderItemResponse> orderItemResponses = new ArrayList<>();
        int totalPrice = 0;

        for (CreateOrderRequest.OrderItemRequest itemRequest : itemRequests) {
            Menu menu = menuMap.get(itemRequest.getMenuId());
            if (menu == null) {
                throw new MenuNotFoundException(itemRequest.getMenuId());
            }
            if (!menu.isAvailable()) {
                throw new OrderException(ErrorCode.ORDER_002);
            }

            int subtotal = menu.getPrice() * itemRequest.getQuantity();
            totalPrice += subtotal;

            orderItemResponses.add(OrderResponse.OrderItemResponse.of(
                    menu.getId(),
                    menu.getName(),
                    itemRequest.getQuantity(),
                    menu.getPrice()
            ));
        }

        // 2. 포인트 차감 (비관적 락 사용)
        int remainingBalance;
        try {
            remainingBalance = userPointService.usePoint(
                    userId,
                    totalPrice,
                    "주문 결제"
            );
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("포인트 잔액이 부족합니다")) {
                throw new OrderException(ErrorCode.ORDER_003);
            }
            throw e;
        }

        // 3. 주문 생성
        Order order = Order.create(userId, totalPrice);
        Order savedOrder = orderRepository.save(order);

        // 4. 주문 상세 생성
        for (CreateOrderRequest.OrderItemRequest itemRequest : itemRequests) {
            Menu menu = menuMap.get(itemRequest.getMenuId());
            OrderItem orderItem = OrderItem.create(
                    savedOrder.getId(),
                    menu.getId(),
                    itemRequest.getQuantity(),
                    menu.getPrice()
            );
            orderItemRepository.save(orderItem);

            // 메뉴 통계 업데이트 (비동기)
            updateMenuStatistics(menu.getId(), itemRequest.getQuantity());
        }

        log.info("주문 생성: orderId={}, userId={}, totalPrice={}, itemCount={}",
                savedOrder.getId(), userId, totalPrice, itemRequests.size());

        // 5. Kafka 이벤트 발행 (비동기)
        publishOrderEvent(savedOrder, orderItemResponses);

        return OrderResponse.of(savedOrder, orderItemResponses, remainingBalance);
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

        // 주문 ID 목록 추출
        List<Long> orderIds = orders.getContent().stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        // 주문 상세 목록 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdIn(orderIds);
        Map<Long, List<OrderItem>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        // 메뉴 정보 조회
        List<Long> menuIds = orderItems.stream()
                .map(OrderItem::getMenuId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Menu> menuMap = menuRepository.findAllByIdIncludingDeleted(menuIds).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu));

        return orders.map(order -> {
            List<OrderItem> items = orderItemMap.getOrDefault(order.getId(), List.of());
            List<OrderHistoryResponse.OrderItemInfo> itemInfos = items.stream()
                    .map(item -> {
                        Menu menu = menuMap.get(item.getMenuId());
                        String menuName = menu != null ? menu.getName() : "삭제된 메뉴";
                        return OrderHistoryResponse.OrderItemInfo.of(
                                menuName,
                                item.getQuantity(),
                                item.getPrice()
                        );
                    })
                    .collect(Collectors.toList());
            return OrderHistoryResponse.of(order, itemInfos);
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

        // 주문 ID 목록 추출
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        // 주문 상세 목록 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdIn(orderIds);
        Map<Long, List<OrderItem>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        // 메뉴 정보 조회
        List<Long> menuIds = orderItems.stream()
                .map(OrderItem::getMenuId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Menu> menuMap = menuRepository.findAllByIdIncludingDeleted(menuIds).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu));

        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMap.getOrDefault(order.getId(), List.of());
                    List<OrderHistoryResponse.OrderItemInfo> itemInfos = items.stream()
                            .map(item -> {
                                Menu menu = menuMap.get(item.getMenuId());
                                String menuName = menu != null ? menu.getName() : "삭제된 메뉴";
                                return OrderHistoryResponse.OrderItemInfo.of(
                                        menuName,
                                        item.getQuantity(),
                                        item.getPrice()
                                );
                            })
                            .collect(Collectors.toList());
                    return OrderHistoryResponse.of(order, itemInfos);
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
    private void updateMenuStatistics(Long menuId, int quantity) {
        try {
            LocalDate today = LocalDate.now();
            MenuStatistics statistics = menuStatisticsRepository
                    .findByMenuIdAndOrderDate(menuId, today)
                    .orElse(null);

            if (statistics == null) {
                // 오늘 날짜의 통계가 없으면 생성
                statistics = MenuStatistics.createToday(menuId);
                statistics.incrementOrderCount(quantity);
                menuStatisticsRepository.save(statistics);
            } else {
                // 기존 통계 업데이트
                statistics.incrementOrderCount(quantity);
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
    private void publishOrderEvent(Order order, List<OrderResponse.OrderItemResponse> items) {
        try {
            String itemsJson = items.stream()
                    .map(item -> String.format("{\"menuId\":%d,\"menuName\":\"%s\",\"quantity\":%d,\"price\":%d}",
                            item.getMenuId(), item.getMenuName(), item.getQuantity(), item.getPrice()))
                    .collect(Collectors.joining(","));

            String message = String.format(
                    "{\"orderId\":%d,\"userId\":%d,\"totalPrice\":%d,\"orderTime\":\"%s\",\"items\":[%s]}",
                    order.getId(),
                    order.getUserId(),
                    order.getTotalPrice(),
                    order.getOrderTime(),
                    itemsJson
            );

            kafkaTemplate.send(ORDER_TOPIC, order.getId().toString(), message);
            log.debug("Kafka 이벤트 발행: orderId={}", order.getId());
        } catch (Exception e) {
            log.error("Kafka 이벤트 발행 실패: orderId={}", order.getId(), e);
            // Kafka 발행 실패는 주문 프로세스에 영향을 주지 않음
        }
    }
}
