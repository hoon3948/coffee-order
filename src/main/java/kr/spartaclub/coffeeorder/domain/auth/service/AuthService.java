package kr.spartaclub.coffeeorder.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spartaclub.coffeeorder.domain.user.entity.User;
import kr.spartaclub.coffeeorder.domain.user.repository.UserRepository;
import kr.spartaclub.coffeeorder.domain.user.service.UserService;
import kr.spartaclub.coffeeorder.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * 로그인
     * @param email 이메일
     * @param password 비밀번호 (평문)
     * @return JWT 토큰
     */
    public String login(String email, String password) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

        return accessToken;
    }

    /**
     * 회원가입
     * @param email 이메일
     * @param password 비밀번호 (평문)
     * @param name 이름
     * @return JWT 토큰
     */
    @Transactional
    public String signUp(String email, String password, String name) {
        // 회원가입
        User user = userService.signUp(email, password, name);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        log.info("회원가입 및 로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

        return accessToken;
    }

    /**
     * 토큰에서 사용자 ID 추출
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserId(token);
    }

    /**
     * 토큰에서 이메일 추출
     * @param token JWT 토큰
     * @return 이메일
     */
    public String getEmailFromToken(String token) {
        return jwtTokenProvider.getEmail(token);
    }

    /**
     * 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
