package kr.spartaclub.coffeeorder.global.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 관련 (AUTH)
    AUTH_001(HttpStatus.CONFLICT, "AUTH_001", "이미 가입된 이메일입니다"),
    AUTH_002(HttpStatus.BAD_REQUEST, "AUTH_002", "올바른 이메일 형식을 입력해주세요"),
    AUTH_003(HttpStatus.BAD_REQUEST, "AUTH_003", "비밀번호는 최소 8자, 영문+숫자 조합이어야 합니다"),
    AUTH_004(HttpStatus.UNAUTHORIZED, "AUTH_004", "이메일 또는 비밀번호가 일치하지 않습니다"),
    AUTH_005(HttpStatus.UNAUTHORIZED, "AUTH_005", "인증이 만료되었습니다"),
    AUTH_006(HttpStatus.UNAUTHORIZED, "AUTH_006", "유효하지 않은 토큰입니다"),

    // 사용자 관련 (USER)
    USER_001(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다"),
    USER_002(HttpStatus.BAD_REQUEST, "USER_002", "현재 비밀번호가 일치하지 않습니다"),

    // 포인트 관련 (POINT)
    POINT_001(HttpStatus.BAD_REQUEST, "POINT_001", "충전 금액은 1,000원 이상이어야 합니다"),
    POINT_002(HttpStatus.BAD_REQUEST, "POINT_002", "충전 금액은 1,000,000원을 초과할 수 없습니다"),
    POINT_003(HttpStatus.INTERNAL_SERVER_ERROR, "POINT_003", "충전에 실패했습니다"),
    POINT_004(HttpStatus.BAD_REQUEST, "POINT_004", "포인트 잔액이 부족합니다"),
    POINT_005(HttpStatus.NOT_FOUND, "POINT_005", "포인트 계정을 찾을 수 없습니다"),

    // 메뉴 관련 (MENU)
    MENU_001(HttpStatus.CONFLICT, "MENU_001", "이미 존재하는 메뉴명입니다"),
    MENU_002(HttpStatus.BAD_REQUEST, "MENU_002", "가격은 100원 ~ 100,000원 사이여야 합니다"),
    MENU_003(HttpStatus.FORBIDDEN, "MENU_003", "관리자 권한이 필요합니다"),
    MENU_004(HttpStatus.NOT_FOUND, "MENU_004", "존재하지 않는 메뉴입니다"),
    MENU_005(HttpStatus.BAD_REQUEST, "MENU_005", "유효하지 않은 상태값입니다"),

    // 주문 관련 (ORDER)
    ORDER_001(HttpStatus.NOT_FOUND, "ORDER_001", "존재하지 않는 메뉴입니다"),
    ORDER_002(HttpStatus.BAD_REQUEST, "ORDER_002", "해당 메뉴는 현재 품절입니다"),
    ORDER_003(HttpStatus.BAD_REQUEST, "ORDER_003", "포인트가 부족합니다"),
    ORDER_004(HttpStatus.TOO_MANY_REQUESTS, "ORDER_004", "잠시 후 다시 시도해주세요"),
    ORDER_005(HttpStatus.INTERNAL_SERVER_ERROR, "ORDER_005", "주문에 실패했습니다"),
    ORDER_006(HttpStatus.NOT_FOUND, "ORDER_006", "존재하지 않는 주문입니다"),

    // 관리자 관련 (ADMIN)
    ADMIN_001(HttpStatus.FORBIDDEN, "ADMIN_001", "관리자 권한이 없습니다"),
    ADMIN_002(HttpStatus.UNAUTHORIZED, "ADMIN_002", "이메일 또는 비밀번호가 일치하지 않습니다"),

    // 공통 (COMMON)
    COMMON_001(HttpStatus.BAD_REQUEST, "COMMON_001", "입력값 검증에 실패했습니다"),
    COMMON_002(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_002", "서버 오류가 발생했습니다"),
    COMMON_003(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_003", "지원하지 않는 HTTP 메서드입니다"),
    COMMON_004(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "COMMON_004", "지원하지 않는 미디어 타입입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
