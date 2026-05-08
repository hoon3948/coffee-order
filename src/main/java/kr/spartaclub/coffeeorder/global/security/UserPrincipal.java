package kr.spartaclub.coffeeorder.global.security;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 인증된 사용자 정보를 담는 Principal 클래스
 */
@Getter
@RequiredArgsConstructor
public class UserPrincipal implements Serializable {
    
    private final Long userId;
    private final String email;
    private final String role;
    
    public static UserPrincipal of(Long userId, String email, String role) {
        return new UserPrincipal(userId, email, role);
    }
}
