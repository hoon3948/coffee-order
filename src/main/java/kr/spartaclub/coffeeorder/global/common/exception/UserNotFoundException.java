package kr.spartaclub.coffeeorder.global.common.exception;

/**
 * 사용자를 찾을 수 없는 예외
 */
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.USER_001);
    }

    public UserNotFoundException(Long userId) {
        super(ErrorCode.USER_001, "사용자를 찾을 수 없습니다: " + userId);
    }
}
