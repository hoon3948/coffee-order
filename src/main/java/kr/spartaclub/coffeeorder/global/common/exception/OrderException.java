package kr.spartaclub.coffeeorder.global.common.exception;

/**
 * 주문 관련 예외
 */
public class OrderException extends BusinessException {

    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OrderException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
