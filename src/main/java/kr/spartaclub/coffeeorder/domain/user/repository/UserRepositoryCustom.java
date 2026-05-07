package kr.spartaclub.coffeeorder.domain.user.repository;

import java.time.LocalDateTime;

import kr.spartaclub.coffeeorder.domain.user.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.spartaclub.coffeeorder.domain.user.entity.User;

/**
 * 사용자 Repository Custom Interface
 * QueryDSL을 사용한 동적 쿼리
 */
public interface UserRepositoryCustom {

    /**
     * 사용자 검색 (이메일, 이름, 역할)
     * @param email 이메일 (부분 검색)
     * @param name 이름 (부분 검색)
     * @param role 역할
     * @param pageable 페이징 정보
     * @return Page<User>
     */
    Page<User> searchUsers(String email, String name, UserRole role, Pageable pageable);

    /**
     * 특정 기간에 가입한 사용자 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return Page<User>
     */
    Page<User> findUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
