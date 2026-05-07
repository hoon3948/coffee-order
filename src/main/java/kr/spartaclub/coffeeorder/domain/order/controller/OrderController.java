package kr.spartaclub.coffeeorder.domain.order.controller;

import jakarta.validation.Valid;
import kr.spartaclub.coffeeorder.global.common.response.ApiResponse;
import kr.spartaclub.coffeeorder.domain.order.dto.CreateOrderRequest;
import kr.spartaclub.coffeeorder.domain.order.dto.OrderHistoryResponse;
import kr.spartaclub.coffeeorder.domain.order.dto.OrderResponse;
import kr.spartaclub.coffeeorder.domain.order.service.OrderService;
import kr.spartaclub.coffeeorder.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 주문 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성
     * POST /api/v1/orders
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @CurrentUser Long userId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        log.info("주문 생성 요청: userId={}, menuId={}", userId, request.getMenuId());

        OrderResponse order = orderService.createOrder(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "주문이 완료되었습니다"));
    }

    /**
     * 주문 내역 조회 (최근 30일)
     * GET /api/v1/orders
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderHistory(
            @CurrentUser Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("주문 내역 조회: userId={}, page={}, size={}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderHistoryResponse> orders = orderService.getOrderHistory(userId, pageable);

        Map<String, Object> data = Map.of(
                "orders", orders.getContent(),
                "totalElements", orders.getTotalElements(),
                "totalPages", orders.getTotalPages(),
                "currentPage", orders.getNumber()
        );

        return ResponseEntity.ok(ApiResponse.success(data, "주문 내역 조회 성공"));
    }

    /**
     * 최근 주문 내역 조회 (간단 버전)
     * GET /api/v1/orders/recent
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<Map<String, List<OrderHistoryResponse>>>> getRecentOrders(
            @CurrentUser Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("최근 주문 내역 조회: userId={}, limit={}", userId, limit);

        List<OrderHistoryResponse> orders = orderService.getRecentOrders(userId, limit);
        Map<String, List<OrderHistoryResponse>> data = Map.of("orders", orders);

        return ResponseEntity.ok(ApiResponse.success(data, "주문 내역 조회 성공"));
    }
}
