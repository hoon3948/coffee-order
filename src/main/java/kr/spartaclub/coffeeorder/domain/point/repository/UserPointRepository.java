package kr.spartaclub.coffeeorder.domain.point.repository;

import java.util.Optional;

import kr.spartaclub.coffeeorder.domain.point.entity.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

/**
 * 사용자 포인트 Repository
 */
@Repository
public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    /**
     * 사용자 ID로 포인트 조회
     * @param userId 사용자 ID
     * @return Optional<UserPoint>
     */
    Optional<UserPoint> findByUserId(Long userId);

    /**
     * 사용자 ID로 포인트 조회 (비관적 락)
     * 동시성 제어를 위해 SELECT ... FOR UPDATE 사용
     * @param userId 사용자 ID
     * @return Optional<UserPoint>
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT up FROM UserPoint up WHERE up.userId = :userId")
    Optional<UserPoint> findByUserIdWithLock(@Param("userId") Long userId);

    /**
     * 사용자 ID로 포인트 존재 여부 확인
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsByUserId(Long userId);

    /**
     * 사용자 ID로 포인트 삭제
     * @param userId 사용자 ID
     */
    void deleteByUserId(Long userId);
}
