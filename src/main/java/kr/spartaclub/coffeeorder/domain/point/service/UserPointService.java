package kr.spartaclub.coffeeorder.domain.point.service;

import kr.spartaclub.coffeeorder.domain.point.entity.UserPoint;
import kr.spartaclub.coffeeorder.domain.point.entity.UserPointHistory;
import kr.spartaclub.coffeeorder.domain.point.enums.PointHistoryType;
import kr.spartaclub.coffeeorder.domain.point.repository.UserPointHistoryRepository;
import kr.spartaclub.coffeeorder.domain.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 포인트 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPointService {

    private final UserPointRepository userPointRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;

    /**
     * 포인트 잔액 조회
     * @param userId 사용자 ID
     * @return 포인트 잔액
     */
    public int getBalance(Long userId) {
        UserPoint userPoint = getUserPoint(userId);
        return userPoint.getBalance();
    }

    /**
     * 포인트 충전
     * @param userId 사용자 ID
     * @param amount 충전 금액
     * @return 충전 후 잔액
     */
    @Transactional
    public int chargePoint(Long userId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        // 포인트 조회 및 충전
        UserPoint userPoint = getUserPoint(userId);
        userPoint.charge(amount);

        // 이력 저장
        UserPointHistory history = UserPointHistory.createChargeHistory(
                userId,
                amount,
                userPoint.getBalance(),
                "포인트 충전"
        );
        userPointHistoryRepository.save(history);

        log.info("포인트 충전: userId={}, amount={}, balance={}", 
                userId, amount, userPoint.getBalance());

        return userPoint.getBalance();
    }

    /**
     * 포인트 사용 (비관적 락 사용)
     * @param userId 사용자 ID
     * @param amount 사용 금액
     * @param description 사용 내역 설명
     * @return 사용 후 잔액
     */
    @Transactional
    public int usePoint(Long userId, int amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }

        // 비관적 락으로 포인트 조회
        UserPoint userPoint = userPointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 계정을 찾을 수 없습니다: " + userId));

        // 잔액 확인 및 차감
        if (!userPoint.hasEnoughBalance(amount)) {
            throw new IllegalArgumentException(
                    String.format("포인트 잔액이 부족합니다. 현재 잔액: %d, 필요 금액: %d", 
                            userPoint.getBalance(), amount)
            );
        }

        userPoint.use(amount);

        // 이력 저장
        UserPointHistory history = UserPointHistory.createUseHistory(
                userId,
                amount,
                userPoint.getBalance(),
                description
        );
        userPointHistoryRepository.save(history);

        log.info("포인트 사용: userId={}, amount={}, balance={}, description={}", 
                userId, amount, userPoint.getBalance(), description);

        return userPoint.getBalance();
    }

    /**
     * 포인트 이력 조회 (페이징)
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 포인트 이력 목록
     */
    public Page<UserPointHistory> getPointHistory(Long userId, Pageable pageable) {
        return userPointHistoryRepository.findByUserId(userId, pageable);
    }

    /**
     * 포인트 이력 조회 (유형별)
     * @param userId 사용자 ID
     * @param type 포인트 이력 유형
     * @param pageable 페이징 정보
     * @return 포인트 이력 목록
     */
    public Page<UserPointHistory> getPointHistoryByType(
            Long userId,
            PointHistoryType type,
            Pageable pageable
    ) {
        return userPointHistoryRepository.findByUserIdAndType(userId, type, pageable);
    }

    /**
     * 포인트 이력 조회 (기간별)
     * @param userId 사용자 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return 포인트 이력 목록
     */
    public Page<UserPointHistory> getPointHistoryByDateRange(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        return userPointHistoryRepository.findByUserIdAndDateRange(
                userId, startDate, endDate, pageable
        );
    }

    /**
     * 최근 포인트 이력 조회
     * @param userId 사용자 ID
     * @param limit 조회 개수
     * @return 포인트 이력 목록
     */
    public List<UserPointHistory> getRecentPointHistory(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return userPointHistoryRepository.findRecentHistories(userId, pageable);
    }

    /**
     * 총 충전 금액 조회
     * @param userId 사용자 ID
     * @return 총 충전 금액
     */
    public int getTotalChargeAmount(Long userId) {
        return userPointHistoryRepository.getTotalChargeAmount(userId);
    }

    /**
     * 총 사용 금액 조회
     * @param userId 사용자 ID
     * @return 총 사용 금액
     */
    public int getTotalUseAmount(Long userId) {
        return userPointHistoryRepository.getTotalUseAmount(userId);
    }

    /**
     * 포인트 잔액 확인
     * @param userId 사용자 ID
     * @param amount 필요 금액
     * @return 잔액 충분 여부
     */
    public boolean hasEnoughBalance(Long userId, int amount) {
        UserPoint userPoint = getUserPoint(userId);
        return userPoint.hasEnoughBalance(amount);
    }

    /**
     * 포인트 계정 조회 (내부 메서드)
     */
    private UserPoint getUserPoint(Long userId) {
        return userPointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 계정을 찾을 수 없습니다: " + userId));
    }
}
