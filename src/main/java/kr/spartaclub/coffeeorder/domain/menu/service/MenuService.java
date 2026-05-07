package kr.spartaclub.coffeeorder.domain.menu.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spartaclub.coffeeorder.global.common.exception.BusinessException;
import kr.spartaclub.coffeeorder.global.common.exception.ErrorCode;
import kr.spartaclub.coffeeorder.global.common.exception.MenuNotFoundException;
import kr.spartaclub.coffeeorder.domain.menu.dto.CreateMenuRequest;
import kr.spartaclub.coffeeorder.domain.menu.dto.MenuResponse;
import kr.spartaclub.coffeeorder.domain.menu.dto.PopularMenuResponse;
import kr.spartaclub.coffeeorder.domain.menu.dto.UpdateMenuRequest;
import kr.spartaclub.coffeeorder.domain.menu.entity.Menu;
import kr.spartaclub.coffeeorder.domain.menu.enums.MenuStatus;
import kr.spartaclub.coffeeorder.domain.menu.repository.MenuRepository;
import kr.spartaclub.coffeeorder.domain.menu.repository.MenuStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메뉴 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuStatisticsRepository menuStatisticsRepository;

    /**
     * 전체 메뉴 조회 (판매 가능한 메뉴만)
     * @return 메뉴 목록
     */
    public List<MenuResponse> getAllMenus() {
        List<Menu> menus = menuRepository.findAvailableMenus();
        return menus.stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 메뉴 단건 조회
     * @param menuId 메뉴 ID
     * @return 메뉴 정보
     */
    public MenuResponse getMenu(Long menuId) {
        Menu menu = findMenuById(menuId);
        return MenuResponse.from(menu);
    }

    /**
     * 인기 메뉴 조회 (최근 7일, 상위 3개)
     * @return 인기 메뉴 목록
     */
    @Cacheable(value = "popularMenus", key = "'last7days'")
    public List<PopularMenuResponse> getPopularMenus() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        
        List<MenuStatisticsRepository.PopularMenuProjection> popularMenus = 
                menuStatisticsRepository.findPopularMenus(startDate, endDate, 3);

        List<PopularMenuResponse> responses = new java.util.ArrayList<>();
        int rank = 1;
        for (MenuStatisticsRepository.PopularMenuProjection projection : popularMenus) {
            Menu menu = findMenuById(projection.getMenuId());
            responses.add(PopularMenuResponse.from(menu, projection.getTotalCount(), rank++));
        }
        
        return responses;
    }

    /**
     * 메뉴 등록 (관리자)
     * @param request 메뉴 등록 요청
     * @return 생성된 메뉴 정보
     */
    @Transactional
    @CacheEvict(value = "popularMenus", allEntries = true)
    public MenuResponse createMenu(CreateMenuRequest request) {
        // 메뉴명 중복 확인
        if (menuRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.MENU_001);
        }

        // 가격 유효성 검증
        if (request.getPrice() < 100 || request.getPrice() > 100000) {
            throw new BusinessException(ErrorCode.MENU_002);
        }

        Menu menu = Menu.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .ingredients(request.getIngredients())
                .status(MenuStatus.AVAILABLE)
                .build();

        Menu savedMenu = menuRepository.save(menu);
        log.info("메뉴 등록: menuId={}, name={}, price={}", 
                savedMenu.getId(), savedMenu.getName(), savedMenu.getPrice());

        return MenuResponse.from(savedMenu);
    }

    /**
     * 메뉴 수정 (관리자)
     * @param menuId 메뉴 ID
     * @param request 메뉴 수정 요청
     * @return 수정된 메뉴 정보
     */
    @Transactional
    @CacheEvict(value = "popularMenus", allEntries = true)
    public MenuResponse updateMenu(Long menuId, UpdateMenuRequest request) {
        Menu menu = findMenuById(menuId);

        // 메뉴명 중복 확인 (다른 메뉴와 중복되는지)
        if (request.getName() != null && !request.getName().equals(menu.getName())) {
            if (menuRepository.existsByName(request.getName())) {
                throw new BusinessException(ErrorCode.MENU_001);
            }
        }

        // 가격 유효성 검증
        if (request.getPrice() != null && (request.getPrice() < 100 || request.getPrice() > 100000)) {
            throw new BusinessException(ErrorCode.MENU_002);
        }

        menu.update(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getIngredients()
        );

        log.info("메뉴 수정: menuId={}, name={}", menuId, menu.getName());

        return MenuResponse.from(menu);
    }

    /**
     * 메뉴 상태 변경 (관리자)
     * @param menuId 메뉴 ID
     * @param status 변경할 상태
     * @return 수정된 메뉴 정보
     */
    @Transactional
    @CacheEvict(value = "popularMenus", allEntries = true)
    public MenuResponse updateMenuStatus(Long menuId, MenuStatus status) {
        Menu menu = findMenuById(menuId);
        menu.changeStatus(status);

        log.info("메뉴 상태 변경: menuId={}, status={}", menuId, status);

        return MenuResponse.from(menu);
    }

    /**
     * 메뉴 삭제 (관리자) - 논리 삭제
     * @param menuId 메뉴 ID
     */
    @Transactional
    @CacheEvict(value = "popularMenus", allEntries = true)
    public void deleteMenu(Long menuId) {
        Menu menu = findMenuById(menuId);
        menuRepository.delete(menu);

        log.info("메뉴 삭제: menuId={}, name={}", menuId, menu.getName());
    }

    /**
     * 메뉴 조회 (내부 메서드)
     */
    private Menu findMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));
    }

    /**
     * 메뉴 판매 가능 여부 확인
     * @param menuId 메뉴 ID
     * @return 판매 가능 여부
     */
    public boolean isMenuAvailable(Long menuId) {
        Menu menu = findMenuById(menuId);
        return menu.isAvailable();
    }
}
