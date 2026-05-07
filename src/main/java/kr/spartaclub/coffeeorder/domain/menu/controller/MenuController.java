package kr.spartaclub.coffeeorder.domain.menu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.spartaclub.coffeeorder.global.common.response.ApiResponse;
import kr.spartaclub.coffeeorder.domain.menu.dto.MenuResponse;
import kr.spartaclub.coffeeorder.domain.menu.dto.PopularMenuResponse;
import kr.spartaclub.coffeeorder.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메뉴 컨트롤러 (사용자용)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 전체 메뉴 조회
     * GET /api/v1/menus
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<MenuResponse>>>> getAllMenus() {
        log.info("전체 메뉴 조회");

        List<MenuResponse> menus = menuService.getAllMenus();
        Map<String, List<MenuResponse>> data = Map.of("menus", menus);

        return ResponseEntity.ok(ApiResponse.success(data, "메뉴 조회 성공"));
    }

    /**
     * 메뉴 단건 조회
     * GET /api/v1/menus/{menuId}
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenu(@PathVariable Long menuId) {
        log.info("메뉴 조회: menuId={}", menuId);

        MenuResponse menu = menuService.getMenu(menuId);

        return ResponseEntity.ok(ApiResponse.success(menu, "메뉴 조회 성공"));
    }

    /**
     * 인기 메뉴 조회
     * GET /api/v1/menus/popular
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Map<String, List<PopularMenuResponse>>>> getPopularMenus() {
        log.info("인기 메뉴 조회");

        List<PopularMenuResponse> popularMenus = menuService.getPopularMenus();
        Map<String, List<PopularMenuResponse>> data = Map.of("popularMenus", popularMenus);

        return ResponseEntity.ok(ApiResponse.success(data, "인기 메뉴 조회 성공"));
    }
}
