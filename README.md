# Coffee Order System

커피숍 주문 시스템 - 포인트 기반 주문 및 인기 메뉴 조회 서비스

## 📋 프로젝트 개요

- **개발 기간**: 1주일
- **기술 스택**: Java 17, Spring Boot 3.x, MySQL 8, Redis, Kafka, Docker
- **핵심 기능**: 포인트 충전/사용, 메뉴 주문, 인기 메뉴 조회, 관리자 메뉴 관리

## 🛠 기술 스택

### Backend
- Java 17
- Spring Boot 3.5.x
- Spring Data JPA
- Spring Security + JWT
- QueryDSL 5.0

### Database & Cache
- MySQL 8.0
- Redis 7

### Message Queue
- Apache Kafka 7.5

### Infrastructure
- Docker & Docker Compose
- Gradle

## 🚀 시작하기

### 1. 환경 변수 설정

`.env` 파일을 생성하고 환경 변수를 설정합니다:

```bash
cp .env.example .env
# .env 파일을 열어 필요한 값들을 수정하세요
```

### 2. Docker 컨테이너 실행

```bash
# Docker Compose로 MySQL, Redis, Kafka 실행
docker-compose up -d

# 컨테이너 상태 확인
docker-compose ps
```

### 3. 애플리케이션 실행

```bash
# Gradle로 빌드 및 실행
./gradlew clean build
./gradlew bootRun

# 또는 IDE에서 CoffeeOrderApplication 실행
```

### 4. 접속 확인

- **애플리케이션**: http://localhost:8080
- **Kafka UI**: http://localhost:8989

## 📁 프로젝트 구조

```
coffee-order/
├── docs/                           # 문서
│   └── SA/                        # Software Architecture 문서
├── docker/                        # Docker 관련 파일
│   └── mysql/
│       └── init/                  # MySQL 초기화 스크립트
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── kr/spartaclub/coffeeorder/
│   │   │       ├── common/       # 공통 클래스 (BaseTimeEntity 등)
│   │   │       ├── domain/       # 도메인 모델
│   │   │       │   ├── auth/     # 인증
│   │   │       │   ├── menu/     # 메뉴
│   │   │       │   ├── order/    # 주문 (Order, OrderItem)
│   │   │       │   ├── point/    # 포인트
│   │   │       │   └── user/     # 사용자
│   │   │       ├── global/       # 전역 설정
│   │   │       │   ├── config/   # 설정 클래스
│   │   │       │   ├── security/ # 보안 관련 (JWT, UserPrincipal)
│   │   │       │   └── common/   # 공통 예외, 응답
│   │   │       └── CoffeeOrderApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── application-local.yaml
│   └── test/
├── .env                          # 환경 변수 (git ignore)
├── .env.example                  # 환경 변수 예시
├── docker-compose.yml            # Docker Compose 설정
└── build.gradle                  # Gradle 빌드 설정
```

## 🔑 주요 기능

### 사용자 기능
- 회원가입 / 로그인 (JWT 인증)
- 포인트 충전
- 포인트 잔액 조회
- 메뉴 목록 조회
- 인기 메뉴 조회 (최근 7일 기준 TOP 3)
- **메뉴 주문 (여러 메뉴 + 수량 지정 가능, 포인트 차감)**
- 주문 내역 조회 (페이지네이션 지원)

### 관리자 기능
- 메뉴 등록
- 메뉴 수정
- 메뉴 삭제 (논리 삭제)
- 메뉴 품절 처리

## 🔐 인증 방식

JWT (JSON Web Token) 기반 인증을 사용합니다.

### 토큰 구조
- **Access Token**: 1시간 유효
- **토큰 내용**: userId, email, role (USER 또는 ADMIN)
- **관리자 구분**: JWT의 role 필드로 구분 (별도 로그인 엔드포인트 없음)

### API 사용 예시

#### 1. 로그인 (일반 사용자)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user1@example.com","password":"user123"}'
```

#### 2. 메뉴 조회 (인증 필요)
```bash
curl -X GET http://localhost:8080/api/v1/menus \
  -H "Authorization: Bearer {your-jwt-token}"
```

#### 3. 주문 생성 (여러 메뉴 주문)
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer {your-jwt-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"menuId": 1, "quantity": 2},
      {"menuId": 2, "quantity": 1}
    ]
  }'
```

# 응답
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer"
  },
  "message": "로그인이 완료되었습니다."
}
```

#### 2. 로그인 (관리자)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@coffee.com","password":"admin123"}'

# JWT 토큰에 role: "ADMIN" 포함
```

#### 3. 메뉴 조회 (인증 필요)
```bash
curl -X GET http://localhost:8080/api/v1/menus \
  -H "Authorization: Bearer {your-jwt-token}"
```

#### 4. 주문 생성 (여러 메뉴 주문)
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer {your-jwt-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"menuId": 1, "quantity": 2},
      {"menuId": 2, "quantity": 1}
    ]
  }'
```

#### 5. 주문 내역 조회
```bash
curl -X GET "http://localhost:8080/api/v1/orders?page=0&size=20" \
  -H "Authorization: Bearer {your-jwt-token}"
```

## 🗄 데이터베이스

### 테이블 구조

주요 테이블:
- **users**: 사용자 정보
- **user_points**: 사용자 포인트 잔액
- **user_point_history**: 포인트 충전/사용 이력
- **menus**: 메뉴 정보
- **orders**: 주문 정보 (총 금액)
- **order_items**: 주문 상세 (메뉴별 수량, 단가)
- **menu_statistics**: 메뉴별 일일 주문 통계

### 초기 데이터

Docker Compose 실행 시 자동으로 초기 데이터가 삽입됩니다:

**관리자 계정**
- Email: `admin@coffee.com`
- Password: `admin123`

**일반 사용자**
- Email: `user1@example.com` / Password: `user123` (포인트: 50,000원)
- Email: `user2@example.com` / Password: `user123` (포인트: 30,000원)
- Email: `user3@example.com` / Password: `user123` (포인트: 20,000원)

**메뉴**
- 아메리카노 (4,500원)
- 카페라떼 (5,000원)
- 바닐라라떼 (5,500원)
- 카푸치노 (5,000원)
- 에스프레소 (4,000원)
- 카라멜마끼아또 (5,500원)
- 카페모카 (5,500원)
- 아이스티 (4,000원)

## 🔧 개발 환경 설정

### QueryDSL 설정

QueryDSL Q클래스는 자동으로 생성됩니다:

```bash
# Q클래스 생성
./gradlew clean compileJava

# 생성된 Q클래스 위치: src/main/generated
```

### Redis 연결 테스트

```bash
# Redis CLI 접속
docker exec -it coffee-order-redis redis-cli -a redis_password

# 연결 확인
127.0.0.1:6379> ping
PONG
```

### Kafka 토픽 확인

```bash
# Kafka 컨테이너 접속
docker exec -it coffee-order-kafka bash

# 토픽 목록 확인
kafka-topics --bootstrap-server localhost:9092 --list

# 토픽 상세 정보
kafka-topics --bootstrap-server localhost:9092 --describe --topic order.created
```

## 📊 동시성 제어

포인트 차감 시 동시성 문제를 2단계로 제어합니다:

1. **Redis 분산 락**: 여러 서버 인스턴스 간 동시 접근 제어
2. **MySQL 비관적 락**: 단일 서버 내 트랜잭션 격리

### 주문 프로세스
```
1. Redis 분산 락 획득 (lock:user:{userId}:point)
2. 트랜잭션 시작
3. 모든 메뉴 정보 조회 및 검증
4. 총 결제 금액 계산 (각 메뉴 가격 × 수량의 합)
5. 비관적 락으로 포인트 조회 (SELECT ... FOR UPDATE)
6. 포인트 차감
7. 주문 생성 (총 금액 저장)
8. 주문 상세 생성 (각 메뉴별 수량, 단가 저장)
9. 메뉴 통계 업데이트 (수량만큼 증가)
10. Kafka 이벤트 발행
11. 트랜잭션 커밋
12. Redis 분산 락 해제
```

자세한 내용은 [동시성 제어 설계서](docs/SA/09-동시성-제어-설계서.md)를 참고하세요.

## 📚 문서

프로젝트의 상세한 설계 문서는 `docs/SA/` 디렉토리에서 확인할 수 있습니다:

1. [프로젝트 개요서](docs/SA/01-프로젝트-개요서.md)
2. [사용자 시나리오](docs/SA/02-사용자-시나리오.md)
3. [유스케이스 명세서](docs/SA/03-유스케이스-명세서.md)
4. [기능 명세서](docs/SA/04-기능-명세서.md)
5. [ERD 설계서](docs/SA/05-ERD-설계서.md)
6. [API 명세서](docs/SA/06-API-명세서.md)
7. [화면 설계서](docs/SA/07-화면-설계서.md)
8. [인프라 아키텍처 다이어그램](docs/SA/08-인프라-아키텍처-다이어그램.md)
9. [동시성 제어 설계서](docs/SA/09-동시성-제어-설계서.md)
10. [ADR (Architecture Decision Record)](docs/SA/10-ADR.md)

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "kr.spartaclub.coffeeorder.*"
```

## 🐛 트러블슈팅

### Docker 컨테이너가 시작되지 않을 때

```bash
# 컨테이너 로그 확인
docker-compose logs mysql
docker-compose logs redis
docker-compose logs kafka

# 컨테이너 재시작
docker-compose restart
```

### 포트 충돌 시

`.env` 파일에서 포트 번호를 변경하세요:

```env
DB_PORT=3307
REDIS_PORT=6380
SERVER_PORT=8081
```

## ✨ 주요 특징

### 1. 다중 항목 주문 시스템
- 한 번의 주문으로 여러 메뉴를 다양한 수량으로 주문 가능
- 예: 아메리카노 2개 + 카페라떼 1개를 한 번에 주문

### 2. 동시성 제어
- Redis 분산 락 + MySQL 비관적 락으로 포인트 차감 시 race condition 방지
- 여러 서버 인스턴스 환경에서도 안전한 포인트 처리

### 3. 실시간 인기 메뉴
- 최근 7일간 주문 데이터 기반 TOP 3 메뉴 조회
- Redis 캐싱으로 빠른 응답 속도 (TTL 5분)

### 4. 비동기 이벤트 처리
- Kafka를 통한 주문 이벤트 발행
- 외부 시스템 장애 시에도 주문 프로세스 정상 동작

### 5. 페이지네이션
- 주문 내역 조회 시 페이지네이션 지원
- 대량 데이터 조회 시 성능 최적화

## 📝 라이선스

이 프로젝트는 학습 목적으로 작성되었습니다.

## 👥 개발자

- Backend Developer (3개월 경력)
- 프로젝트 기간: 1주일

## 📌 버전 히스토리

### v1.2 (2026-05-08)
- 🔧 인증 시스템 간소화
- 📝 Refresh Token 기능 제거 (Access Token만 사용)
- 🔧 관리자 로그인 통합 (일반 로그인과 동일 엔드포인트, role로 구분)
- 📝 로그인 응답 형식 간소화 (token, tokenType만 반환)
- 📝 문서 업데이트 (기능 명세서, API 명세서)

### v1.1 (2026-05-08)
- ✨ 다중 항목 주문 시스템 구현
- ✨ 주문 내역 페이지네이션 지원
- 🔧 order_items 테이블 추가
- 📝 문서 업데이트 (ERD, API, 기능 명세서)

### v1.0 (2026-05-06)
- 🎉 초기 버전 출시
- ✨ 기본 주문 시스템 구현
- ✨ 포인트 충전/사용 기능
- ✨ 인기 메뉴 조회 기능
- ✨ 관리자 메뉴 관리 기능
