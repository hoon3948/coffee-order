package kr.spartaclub.coffeeorder.global.common.exception;

/**
 * 인증 관련 예외
 */
public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
