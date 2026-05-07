package kr.spartaclub.coffeeorder.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.spartaclub.coffeeorder.domain.user.entity.User;
import kr.spartaclub.coffeeorder.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static kr.spartaclub.coffeeorder.domain.user.entity.QUser.user;


/**
 * 사용자 Repository Custom 구현체
 * QueryDSL을 사용한 동적 쿼리 구현
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> searchUsers(String email, String name, UserRole role, Pageable pageable) {
        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        emailContains(email),
                        nameContains(name),
                        roleEq(role)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        emailContains(email),
                        nameContains(name),
                        roleEq(role)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<User> findUsersCreatedBetween(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        user.createdAt.between(startDate, endDate)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.createdAt.between(startDate, endDate)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 이메일 부분 검색 조건
     */
    private BooleanExpression emailContains(String email) {
        return email != null ? user.email.containsIgnoreCase(email) : null;
    }

    /**
     * 이름 부분 검색 조건
     */
    private BooleanExpression nameContains(String name) {
        return name != null ? user.name.containsIgnoreCase(name) : null;
    }

    /**
     * 역할 일치 조건
     */
    private BooleanExpression roleEq(UserRole role) {
        return role != null ? user.role.eq(role) : null;
    }
}
