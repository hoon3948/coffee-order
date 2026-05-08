-- Coffee Order System Database Initialization Script
-- Version: 1.0
-- Date: 2026-05-06

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE coffee_order;

-- 1. users (사용자)
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '사용자 ID',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일 (로그인 ID)',
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt 해시 비밀번호',
    name VARCHAR(50) NOT NULL COMMENT '사용자 이름',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '역할 (USER, ADMIN)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 테이블';

-- 2. user_points (사용자 포인트)
CREATE TABLE IF NOT EXISTS user_points (
    point_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '포인트 ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '사용자 ID',
    balance INT NOT NULL DEFAULT 0 COMMENT '포인트 잔액',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    CONSTRAINT fk_user_points_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_balance CHECK (balance >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 포인트 테이블';

-- 3. user_point_history (포인트 이력)
CREATE TABLE IF NOT EXISTS user_point_history (
    history_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '이력 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    type VARCHAR(20) NOT NULL COMMENT '유형 (CHARGE, USE)',
    amount INT NOT NULL COMMENT '변동 금액',
    balance_after INT NOT NULL COMMENT '변동 후 잔액',
    description VARCHAR(200) NULL COMMENT '설명',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    CONSTRAINT fk_user_point_history_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='포인트 이력 테이블';

-- 4. menus (메뉴)
CREATE TABLE IF NOT EXISTS menus (
    menu_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '메뉴 ID',
    name VARCHAR(50) NOT NULL COMMENT '메뉴명',
    price INT NOT NULL COMMENT '가격 (원)',
    description TEXT NULL COMMENT '메뉴 설명',
    ingredients JSON NULL COMMENT '원자재 태그',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '메뉴 상태 (AVAILABLE, SOLD_OUT)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    deleted_at DATETIME NULL COMMENT '삭제 시간 (논리 삭제)',
    CONSTRAINT chk_price CHECK (price > 0),
    UNIQUE KEY uk_name_active (name, deleted_at),
    INDEX idx_status_deleted (status, deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='메뉴 테이블';

-- 5. orders (주문)
CREATE TABLE IF NOT EXISTS orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '주문 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    menu_id BIGINT NOT NULL COMMENT '메뉴 ID',
    price INT NOT NULL COMMENT '결제 금액 (주문 시점 가격)',
    order_time DATETIME NOT NULL COMMENT '주문 시간',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_menu FOREIGN KEY (menu_id) REFERENCES menus(menu_id) ON DELETE RESTRICT,
    INDEX idx_user_order_time (user_id, order_time),
    INDEX idx_menu_order_time (menu_id, order_time),
    INDEX idx_order_time (order_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문 테이블';

-- 6. menu_statistics (메뉴 통계)
CREATE TABLE IF NOT EXISTS menu_statistics (
    stat_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '통계 ID',
    menu_id BIGINT NOT NULL COMMENT '메뉴 ID',
    order_date DATE NOT NULL COMMENT '주문 날짜',
    order_count INT NOT NULL DEFAULT 0 COMMENT '주문 횟수',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    CONSTRAINT fk_menu_statistics_menu FOREIGN KEY (menu_id) REFERENCES menus(menu_id) ON DELETE CASCADE,
    UNIQUE KEY uk_menu_date (menu_id, order_date),
    INDEX idx_order_date (order_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='메뉴 통계 테이블';

-- 샘플 데이터 삽입

-- 관리자 계정 (password: admin123)
INSERT INTO users (email, password, name, role) VALUES
('admin@coffee.com', '$2a$10$O3qllRSbB7/SZ3Zl9i0HTucv.WLS2xJklvunQLxO.fqHR2q.96A0.', '관리자', 'ADMIN');

-- 일반 사용자 (password: user1234)
INSERT INTO users (email, password, name, role) VALUES
('user1@example.com', '$2a$10$tJPXXUdNl/0ruuRzJOecI.yyvxEyLFpT57dxzDdkx9.cQ8Ke43Uty', '홍길동', 'USER'),
('user2@example.com', '$2a$10$tJPXXUdNl/0ruuRzJOecI.yyvxEyLFpT57dxzDdkx9.cQ8Ke43Uty', '김철수', 'USER'),
('user3@example.com', '$2a$10$tJPXXUdNl/0ruuRzJOecI.yyvxEyLFpT57dxzDdkx9.cQ8Ke43Uty', '이영희', 'USER');

-- 사용자 포인트 초기화
INSERT INTO user_points (user_id, balance) VALUES
(1, 100000),
(2, 50000),
(3, 30000),
(4, 20000);

-- 메뉴 데이터
INSERT INTO menus (name, price, description, ingredients, status) VALUES
('아메리카노', 4500, '깊고 진한 에스프레소에 물을 더한 클래식 커피', JSON_ARRAY('원두', '물'), 'AVAILABLE'),
('카페라떼', 5000, '부드러운 우유와 에스프레소의 조화', JSON_ARRAY('원두', '우유', '물'), 'AVAILABLE'),
('바닐라라떼', 5500, '달콤한 바닐라 시럽이 들어간 라떼', JSON_ARRAY('원두', '우유', '바닐라시럽', '물'), 'AVAILABLE'),
('카푸치노', 5000, '우유 거품이 풍부한 이탈리안 커피', JSON_ARRAY('원두', '우유', '물'), 'AVAILABLE'),
('에스프레소', 4000, '진한 에스프레소 샷', JSON_ARRAY('원두', '물'), 'AVAILABLE'),
('카라멜마끼아또', 5500, '달콤한 카라멜과 에스프레소', JSON_ARRAY('원두', '우유', '카라멜시럽', '물'), 'AVAILABLE'),
('카페모카', 5500, '초콜릿과 에스프레소의 만남', JSON_ARRAY('원두', '우유', '초콜릿시럽', '물'), 'AVAILABLE'),
('아이스티', 4000, '상큼한 아이스티', JSON_ARRAY('홍차', '물', '얼음'), 'AVAILABLE');

-- 초기 통계 데이터 (최근 7일)
INSERT INTO menu_statistics (menu_id, order_date, order_count) VALUES
(1, CURDATE() - INTERVAL 1 DAY, 25),
(2, CURDATE() - INTERVAL 1 DAY, 18),
(3, CURDATE() - INTERVAL 1 DAY, 12),
(1, CURDATE() - INTERVAL 2 DAY, 30),
(2, CURDATE() - INTERVAL 2 DAY, 22),
(4, CURDATE() - INTERVAL 2 DAY, 15);

COMMIT;
