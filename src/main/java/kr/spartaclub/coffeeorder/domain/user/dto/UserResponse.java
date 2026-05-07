package kr.spartaclub.coffeeorder.domain.user.dto;

import kr.spartaclub.coffeeorder.domain.user.entity.User;
import kr.spartaclub.coffeeorder.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String email;
    private String name;
    private UserRole role;
    private LocalDateTime createdAt;

    /**
     * Entity -> Response 변환
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
