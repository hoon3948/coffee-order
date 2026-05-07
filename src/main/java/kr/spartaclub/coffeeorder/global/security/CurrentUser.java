package kr.spartaclub.coffeeorder.global.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 현재 로그인한 사용자 ID를 주입받기 위한 어노테이션
 * 
 * 사용 예시:
 * public ResponseEntity<?> getMyInfo(@CurrentUser Long userId) {
 *     // userId는 JWT 토큰에서 추출된 사용자 ID
 * }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : #this.name")
public @interface CurrentUser {
}
