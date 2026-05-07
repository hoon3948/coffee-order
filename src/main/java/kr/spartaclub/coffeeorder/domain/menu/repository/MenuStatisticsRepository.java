package kr.spartaclub.coffeeorder.domain.menu.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.spartaclub.coffeeorder.domain.menu.entity.MenuStatistics;

/**
 * 메뉴 통계 Repository
 */
@Repository
public interface MenuStatisticsRepository extends JpaRepository<MenuStatistics, Long> {

    /**
     * 메뉴 ID와 날짜로 통계 조회
     * @param menuId 메뉴 ID
     * @param orderDate 주문 날짜
     * @return Optional<MenuStatistics>
     */
    Optional<MenuStatistics> findByMenuIdAndOrderDate(Long menuId, LocalDate orderDate);

    /**
     * 특정 기간의 인기 메뉴 조회 (주문 횟수 기준 상위 N개)
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param limit 조회 개수
     * @return 메뉴 통계 목록
     */
    @Query(value = """
            SELECT ms.menu_id as menuId, SUM(ms.order_count) as totalCount
            FROM menu_statistics ms
            WHERE ms.order_date BETWEEN :startDate AND :endDate
            GROUP BY ms.menu_id
            ORDER BY totalCount DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<PopularMenuProjection> findPopularMenus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit
    );

    /**
     * 인기 메뉴 조회용 Projection
     */
    interface PopularMenuProjection {
        Long getMenuId();
        Long getTotalCount();
    }

    /**
     * 특정 날짜 이전의 통계 삭제
     * @param date 기준 날짜
     */
    void deleteByOrderDateBefore(LocalDate date);
}
