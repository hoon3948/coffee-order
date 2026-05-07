package kr.spartaclub.coffeeorder.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 역할 Enum
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("일반 사용자"),
    ADMIN("관리자");

    private final String description;
}
