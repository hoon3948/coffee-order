package kr.spartaclub.coffeeorder.domain.point.repository;

import kr.spartaclub.coffeeorder.domain.point.entity.UserPointHistory;
import kr.spartaclub.coffeeorder.domain.point.enums.PointHistoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 포인트 이력 Repository
 */
@Repository
public interface UserPointHistoryRepository extends JpaRepository<UserPointHistory, Long> {

    /**
     * 사용자 ID로 포인트 이력 조회 (페이징)
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return Page<UserPointHistory>
     */
    Page<UserPointHistory> findByUserId(Long userId, Pageable pageable);

    /**
     * 사용자 ID로 포인트 이력 조회 (최신순)
     * @param userId 사용자 ID
     * @return 포인트 이력 목록
     */
    List<UserPointHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 사용자 ID와 유형으로 포인트 이력 조회
     * @param userId 사용자 ID
     * @param type 포인트 이력 유형
     * @param pageable 페이징 정보
     * @return Page<UserPointHistory>
     */
    Page<UserPointHistory> findByUserIdAndType(Long userId, PointHistoryType type, Pageable pageable);

    /**
     * 사용자 ID와 기간으로 포인트 이력 조회
     * @param userId 사용자 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return Page<UserPointHistory>
     */
    @Query("SELECT uph FROM UserPointHistory uph " +
           "WHERE uph.userId = :userId " +
           "AND uph.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY uph.createdAt DESC")
    Page<UserPointHistory> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * 사용자 ID로 총 충전 금액 조회
     * @param userId 사용자 ID
     * @return 총 충전 금액
     */
    @Query("SELECT COALESCE(SUM(uph.amount), 0) FROM UserPointHistory uph " +
           "WHERE uph.userId = :userId AND uph.type = 'CHARGE'")
    Integer getTotalChargeAmount(@Param("userId") Long userId);

    /**
     * 사용자 ID로 총 사용 금액 조회
     * @param userId 사용자 ID
     * @return 총 사용 금액 (절댓값)
     */
    @Query("SELECT COALESCE(ABS(SUM(uph.amount)), 0) FROM UserPointHistory uph " +
           "WHERE uph.userId = :userId AND uph.type = 'USE'")
    Integer getTotalUseAmount(@Param("userId") Long userId);

    /**
     * 사용자 ID로 최근 N개 이력 조회
     * @param userId 사용자 ID
     * @param pageable 페이징 정보 (size로 개수 제한)
     * @return 포인트 이력 목록
     */
    @Query("SELECT uph FROM UserPointHistory uph " +
           "WHERE uph.userId = :userId " +
           "ORDER BY uph.createdAt DESC")
    List<UserPointHistory> findRecentHistories(@Param("userId") Long userId, Pageable pageable);
}
