package kr.spartaclub.coffeeorder.domain.user.repository;

import java.util.List;
import java.util.Optional;

import kr.spartaclub.coffeeorder.domain.user.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.spartaclub.coffeeorder.domain.user.entity.User;

/**
 * 사용자 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 중복 확인
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 역할별 사용자 목록 조회
     * @param role 사용자 역할
     * @return 사용자 목록
     */
    List<User> findByRole(UserRole role);

    /**
     * 이름으로 사용자 검색 (LIKE 검색)
     * @param name 이름
     * @return 사용자 목록
     */
    List<User> findByNameContaining(String name);
}
