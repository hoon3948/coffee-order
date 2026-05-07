package kr.spartaclub.coffeeorder.domain.point.controller;

import kr.spartaclub.coffeeorder.domain.point.entity.UserPointHistory;
import kr.spartaclub.coffeeorder.domain.point.service.UserPointService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.spartaclub.coffeeorder.global.common.response.ApiResponse;
import kr.spartaclub.coffeeorder.domain.point.dto.ChargePointRequest;
import kr.spartaclub.coffeeorder.domain.point.dto.PointHistoryResponse;
import kr.spartaclub.coffeeorder.domain.point.dto.PointResponse;
import kr.spartaclub.coffeeorder.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 포인트 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class UserPointController {

    private final UserPointService userPointService;

    /**
     * 포인트 잔액 조회
     * GET /api/v1/points
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<PointResponse>> getBalance(@CurrentUser Long userId) {
        log.info("포인트 잔액 조회: userId={}", userId);

        int balance = userPointService.getBalance(userId);
        PointResponse response = PointResponse.of(userId, balance);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 포인트 충전
     * POST /api/v1/points/charge
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<PointResponse>> chargePoint(
            @CurrentUser Long userId,
            @Valid @RequestBody ChargePointRequest request
    ) {
        log.info("포인트 충전 요청: userId={}, amount={}", userId, request.getAmount());

        int balance = userPointService.chargePoint(userId, request.getAmount());
        PointResponse response = PointResponse.of(
                userId,
                balance,
                String.format("%,d원이 충전되었습니다.", request.getAmount())
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 포인트 이력 조회
     * GET /api/v1/points/history
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<PointHistoryResponse>>> getPointHistory(
            @CurrentUser Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("포인트 이력 조회: userId={}, page={}, size={}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<UserPointHistory> historyPage = userPointService.getPointHistory(userId, pageable);
        Page<PointHistoryResponse> response = historyPage.map(PointHistoryResponse::from);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
