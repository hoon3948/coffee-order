package kr.spartaclub.coffeeorder.domain.point.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포인트 이력 유형 Enum
 */
@Getter
@RequiredArgsConstructor
public enum PointHistoryType {
    CHARGE("충전"),
    USE("사용");

    private final String description;
}
