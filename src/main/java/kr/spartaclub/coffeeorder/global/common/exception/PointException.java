package kr.spartaclub.coffeeorder.global.common.exception;

/**
 * 포인트 관련 예외
 */
public class PointException extends BusinessException {

    public PointException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PointException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
