package kr.spartaclub.coffeeorder.domain.user.service;

import kr.spartaclub.coffeeorder.domain.point.entity.UserPoint;
import kr.spartaclub.coffeeorder.domain.point.repository.UserPointRepository;
import kr.spartaclub.coffeeorder.domain.user.entity.User;
import kr.spartaclub.coffeeorder.domain.user.enums.UserRole;
import kr.spartaclub.coffeeorder.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 사용자 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @param email 이메일
     * @param password 비밀번호 (평문)
     * @param name 이름
     * @return 생성된 사용자
     */
    @Transactional
    public User signUp(String email, String password, String name) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 생성
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        // 포인트 계정 생성 (초기 잔액 0)
        UserPoint userPoint = UserPoint.builder()
                .userId(savedUser.getId())
                .balance(0)
                .build();
        userPointRepository.save(userPoint);
        log.info("포인트 계정 생성: userId={}, balance=0", savedUser.getId());

        return savedUser;
    }

    /**
     * 관리자 계정 생성
     * @param email 이메일
     * @param password 비밀번호 (평문)
     * @param name 이름
     * @return 생성된 관리자
     */
    @Transactional
    public User createAdmin(String email, String password, String name) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 관리자 생성
        User admin = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(UserRole.ADMIN)
                .build();

        User savedAdmin = userRepository.save(admin);
        log.info("관리자 계정 생성: userId={}, email={}", savedAdmin.getId(), savedAdmin.getEmail());

        // 포인트 계정 생성
        UserPoint userPoint = UserPoint.builder()
                .userId(savedAdmin.getId())
                .balance(0)
                .build();
        userPointRepository.save(userPoint);

        return savedAdmin;
    }

    /**
     * 사용자 조회 (ID)
     * @param userId 사용자 ID
     * @return 사용자
     */
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
    }

    /**
     * 사용자 조회 (이메일)
     * @param email 이메일
     * @return 사용자
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }

    /**
     * 사용자 검색
     * @param email 이메일 (부분 검색)
     * @param name 이름 (부분 검색)
     * @param role 역할
     * @param pageable 페이징 정보
     * @return 사용자 목록
     */
    public Page<User> searchUsers(String email, String name, UserRole role, Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * 특정 기간에 가입한 사용자 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return 사용자 목록
     */
    public Page<User> getUsersCreatedBetween(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        return userRepository.findAll(pageable);
    }

    /**
     * 비밀번호 변경
     * @param userId 사용자 ID
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     */
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUser(userId);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 및 변경
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedNewPassword);

        log.info("비밀번호 변경 완료: userId={}", userId);
    }

    /**
     * 이름 변경
     * @param userId 사용자 ID
     * @param newName 새 이름
     */
    @Transactional
    public void changeName(Long userId, String newName) {
        User user = getUser(userId);
        user.changeName(newName);
        log.info("이름 변경 완료: userId={}, newName={}", userId, newName);
    }

    /**
     * 사용자 삭제
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        
        // 포인트 계정도 함께 삭제 (CASCADE)
        userRepository.delete(user);
        log.info("사용자 삭제 완료: userId={}, email={}", userId, user.getEmail());
    }

    /**
     * 이메일 중복 확인
     * @param email 이메일
     * @return 중복 여부
     */
    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 관리자 여부 확인
     * @param userId 사용자 ID
     * @return 관리자 여부
     */
    public boolean isAdmin(Long userId) {
        User user = getUser(userId);
        return user.isAdmin();
    }
}
