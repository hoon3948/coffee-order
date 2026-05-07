package kr.spartaclub.coffeeorder.domain.menu.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 메뉴 상태 Enum
 */
@Getter
@RequiredArgsConstructor
public enum MenuStatus {
    AVAILABLE("판매중"),
    SOLD_OUT("품절");

    private final String description;
}
