package kr.spartaclub.coffeeorder.domain.menu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.spartaclub.coffeeorder.domain.menu.entity.Menu;
import kr.spartaclub.coffeeorder.domain.menu.enums.MenuStatus;

/**
 * 메뉴 Repository
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    /**
     * 메뉴명으로 조회
     * @param name 메뉴명
     * @return Optional<Menu>
     */
    Optional<Menu> findByName(String name);

    /**
     * 메뉴명 중복 확인
     * @param name 메뉴명
     * @return 존재 여부
     */
    boolean existsByName(String name);

    /**
     * 상태별 메뉴 조회
     * @param status 메뉴 상태
     * @return 메뉴 목록
     */
    List<Menu> findByStatus(MenuStatus status);

    /**
     * 판매 가능한 메뉴 조회
     * @return 메뉴 목록
     */
    @Query("SELECT m FROM Menu m WHERE m.status = 'AVAILABLE' AND m.deletedAt IS NULL")
    List<Menu> findAvailableMenus();

    /**
     * 메뉴 ID로 조회 (삭제된 것 포함)
     * @param menuId 메뉴 ID
     * @return Optional<Menu>
     */
    @Query("SELECT m FROM Menu m WHERE m.id = :menuId")
    Optional<Menu> findByIdIncludingDeleted(@Param("menuId") Long menuId);

    /**
     * 메뉴 ID 목록으로 조회 (삭제된 것 포함)
     * @param menuIds 메뉴 ID 목록
     * @return 메뉴 목록
     */
    @Query("SELECT m FROM Menu m WHERE m.id IN :menuIds")
    List<Menu> findAllByIdIncludingDeleted(@Param("menuIds") List<Long> menuIds);
}
