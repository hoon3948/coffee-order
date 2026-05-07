package kr.spartaclub.coffeeorder.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인증 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType;

    /**
     * JWT 토큰 응답 생성
     */
    public static AuthResponse of(String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .build();
    }
}
