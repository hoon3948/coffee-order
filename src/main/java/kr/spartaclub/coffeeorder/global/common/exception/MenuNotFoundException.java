package kr.spartaclub.coffeeorder.global.common.exception;

/**
 * 메뉴를 찾을 수 없는 예외
 */
public class MenuNotFoundException extends BusinessException {

    public MenuNotFoundException() {
        super(ErrorCode.MENU_004);
    }

    public MenuNotFoundException(Long menuId) {
        super(ErrorCode.MENU_004, "존재하지 않는 메뉴입니다: " + menuId);
    }
}
