package kr.spartaclub.coffeeorder.global.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 현재 로그인한 사용자 정보를 주입받기 위한 어노테이션
 * 
 * 사용 예시:
 * public ResponseEntity<?> getMyInfo(@CurrentUser UserPrincipal principal) {
 *     Long userId = principal.getUserId();
 * }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
