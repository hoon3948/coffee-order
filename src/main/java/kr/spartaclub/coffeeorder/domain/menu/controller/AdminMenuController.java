package kr.spartaclub.coffeeorder.domain.menu.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.spartaclub.coffeeorder.global.common.response.ApiResponse;
import kr.spartaclub.coffeeorder.domain.menu.dto.CreateMenuRequest;
import kr.spartaclub.coffeeorder.domain.menu.dto.MenuResponse;
import kr.spartaclub.coffeeorder.domain.menu.dto.UpdateMenuRequest;
import kr.spartaclub.coffeeorder.domain.menu.dto.UpdateMenuStatusRequest;
import kr.spartaclub.coffeeorder.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 관리자 메뉴 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/menus")
@RequiredArgsConstructor
public class AdminMenuController {

    private final MenuService menuService;

    /**
     * 메뉴 등록
     * POST /api/v1/admin/menus
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> createMenu(
            @Valid @RequestBody CreateMenuRequest request
    ) {
        log.info("메뉴 등록: name={}, price={}", request.getName(), request.getPrice());

        MenuResponse menu = menuService.createMenu(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(menu, "메뉴 등록 성공"));
    }

    /**
     * 메뉴 수정
     * PATCH /api/v1/admin/menus/{menuId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(
            @PathVariable Long menuId,
            @Valid @RequestBody UpdateMenuRequest request
    ) {
        log.info("메뉴 수정: menuId={}", menuId);

        MenuResponse menu = menuService.updateMenu(menuId, request);

        return ResponseEntity.ok(ApiResponse.success(menu, "메뉴 수정 성공"));
    }

    /**
     * 메뉴 상태 변경
     * PATCH /api/v1/admin/menus/{menuId}/status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{menuId}/status")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenuStatus(
            @PathVariable Long menuId,
            @Valid @RequestBody UpdateMenuStatusRequest request
    ) {
        log.info("메뉴 상태 변경: menuId={}, status={}", menuId, request.getStatus());

        MenuResponse menu = menuService.updateMenuStatus(menuId, request.getStatus());

        return ResponseEntity.ok(ApiResponse.success(menu, "메뉴 상태 변경 성공"));
    }

    /**
     * 메뉴 삭제
     * DELETE /api/v1/admin/menus/{menuId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> deleteMenu(@PathVariable Long menuId) {
        log.info("메뉴 삭제: menuId={}", menuId);

        menuService.deleteMenu(menuId);

        Map<String, Long> data = Map.of("menuId", menuId);
        return ResponseEntity.ok(ApiResponse.success(data, "메뉴 삭제 성공"));
    }
}
