package kr.spartaclub.coffeeorder.domain.auth.controller;

import kr.spartaclub.coffeeorder.domain.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.spartaclub.coffeeorder.global.common.response.ApiResponse;
import kr.spartaclub.coffeeorder.domain.auth.dto.AuthResponse;
import kr.spartaclub.coffeeorder.domain.auth.dto.LoginRequest;
import kr.spartaclub.coffeeorder.domain.auth.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        log.info("회원가입 요청: email={}", request.getEmail());

        String token = authService.signUp(
                request.getEmail(),
                request.getPassword(),
                request.getName()
        );

        AuthResponse response = AuthResponse.of(token);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입이 완료되었습니다."));
    }

    /**
     * 로그인
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("로그인 요청: email={}", request.getEmail());

        String token = authService.login(request.getEmail(), request.getPassword());

        AuthResponse response = AuthResponse.of(token);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
    }
}
