# API 명세서

## 1. 문서 개요

이 문서는 커피숍 주문 시스템의 REST API 명세를 정의합니다.

---

## 2. API 기본 정보

### 2.1 Base URL
```
http://localhost:8080/api/v1
```

### 2.2 공통 헤더

**요청 헤더**:
```http
Content-Type: application/json
Authorization: Bearer {accessToken}  # 인증 필요 API만
```

**응답 헤더**:
```http
Content-Type: application/json
```

### 2.3 공통 응답 형식

**성공 응답**:
```json
{
  "success": true,
  "data": { ... },
  "message": "성공 메시지"
}
```

**에러 응답**:
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지"
  }
}
```

### 2.4 HTTP 상태 코드

| 상태 코드 | 설명 | 사용 예시 |
|-----------|------|-----------|
| 200 OK | 성공 | 조회, 수정, 삭제 성공 |
| 201 Created | 생성 성공 | 회원가입, 주문 생성 |
| 400 Bad Request | 잘못된 요청 | 유효성 검증 실패 |
| 401 Unauthorized | 인증 실패 | 토큰 없음, 만료 |
| 403 Forbidden | 권한 없음 | 관리자 권한 필요 |
| 404 Not Found | 리소스 없음 | 존재하지 않는 메뉴 |
| 409 Conflict | 충돌 | 이메일 중복 |
| 429 Too Many Requests | 요청 과다 | 락 획득 실패 |
| 500 Internal Server Error | 서버 오류 | 예상치 못한 오류 |

---

## 3. 인증 API

### 3.1 회원가입

**Endpoint**: `POST /auth/signup`

**설명**: 신규 사용자 계정을 생성합니다.

**인증**: 불필요

**요청**:
```json
{
  "email": "user@example.com",
  "password": "Password123",
  "name": "홍길동"
}
```

**응답** (201 Created):
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "회원가입이 완료되었습니다"
}
```

**에러**:
- `AUTH_001` (409): 이미 가입된 이메일입니다
- `AUTH_002` (400): 올바른 이메일 형식을 입력해주세요
- `AUTH_003` (400): 비밀번호는 최소 8자, 영문+숫자 조합이어야 합니다

---

### 3.2 로그인

**Endpoint**: `POST /auth/login`

**설명**: 이메일과 비밀번호로 로그인합니다.

**인증**: 불필요

**요청**:
```json
{
  "email": "user@example.com",
  "password": "Password123"
}
```

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "pointBalance": 10000,
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "로그인 성공"
}
```

**에러**:
- `AUTH_004` (401): 이메일 또는 비밀번호가 일치하지 않습니다

---

### 3.3 토큰 갱신

**Endpoint**: `POST /auth/refresh`

**설명**: Refresh Token으로 새 Access Token을 발급받습니다.

**인증**: 불필요 (Refresh Token 필요)

**요청**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "토큰 갱신 성공"
}
```

**에러**:
- `AUTH_005` (401): 인증이 만료되었습니다
- `AUTH_006` (401): 유효하지 않은 토큰입니다

---

## 4. 메뉴 API

### 4.1 전체 메뉴 조회

**Endpoint**: `GET /menus`

**설명**: 현재 판매중인 모든 메뉴를 조회합니다.

**인증**: 필요 (USER, ADMIN)

**요청**: 없음

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "menus": [
      {
        "menuId": 1,
        "name": "아메리카노",
        "price": 4500,
        "description": "깊고 진한 에스프레소",
        "status": "AVAILABLE"
      },
      {
        "menuId": 2,
        "name": "카페라떼",
        "price": 5000,
        "description": "부드러운 우유와 에스프레소",
        "status": "AVAILABLE"
      }
    ]
  },
  "message": "메뉴 조회 성공"
}
```

**에러**: 없음 (빈 배열 반환 가능)

---

### 4.2 인기 메뉴 조회

**Endpoint**: `GET /menus/popular`

**설명**: 최근 7일간 주문 횟수 기준 상위 3개 메뉴를 조회합니다.

**인증**: 필요 (USER, ADMIN)

**요청**: 없음

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "popularMenus": [
      {
        "menuId": 1,
        "name": "아메리카노",
        "price": 4500,
        "orderCount": 150,
        "rank": 1
      },
      {
        "menuId": 2,
        "name": "카페라떼",
        "price": 5000,
        "orderCount": 120,
        "rank": 2
      },
      {
        "menuId": 3,
        "name": "바닐라라떼",
        "price": 5500,
        "orderCount": 100,
        "rank": 3
      }
    ]
  },
  "message": "인기 메뉴 조회 성공"
}
```

**에러**: 없음 (빈 배열 반환 가능)

---

## 5. 포인트 API

### 5.1 포인트 충전

**Endpoint**: `POST /points/charge`

**설명**: 사용자 포인트를 충전합니다.

**인증**: 필요 (USER)

**요청**:
```json
{
  "amount": 10000
}
```

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "pointBalance": 20000,
    "chargedAmount": 10000
  },
  "message": "포인트 충전 성공"
}
```

**에러**:
- `POINT_001` (400): 충전 금액은 1,000원 이상이어야 합니다
- `POINT_002` (400): 충전 금액은 1,000,000원을 초과할 수 없습니다
- `POINT_003` (500): 충전에 실패했습니다

---

### 5.2 포인트 조회

**Endpoint**: `GET /points`

**설명**: 현재 포인트 잔액을 조회합니다.

**인증**: 필요 (USER)

**요청**: 없음

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "pointBalance": 20000
  },
  "message": "포인트 조회 성공"
}
```

**에러**: 없음

---

## 6. 주문 API

### 6.1 주문 생성

**Endpoint**: `POST /orders`

**설명**: 메뉴를 주문하고 포인트로 결제합니다.

**인증**: 필요 (USER)

**요청**:
```json
{
  "menuId": 1
}
```

**응답** (201 Created):
```json
{
  "success": true,
  "data": {
    "orderId": 123,
    "menuName": "아메리카노",
    "price": 4500,
    "pointBalance": 15500,
    "orderTime": "2026-05-06T10:30:00"
  },
  "message": "주문이 완료되었습니다"
}
```

**에러**:
- `ORDER_001` (404): 존재하지 않는 메뉴입니다
- `ORDER_002` (400): 해당 메뉴는 현재 품절입니다
- `ORDER_003` (400): 포인트가 부족합니다
- `ORDER_004` (429): 잠시 후 다시 시도해주세요
- `ORDER_005` (500): 주문에 실패했습니다

---

### 6.2 주문 내역 조회

**Endpoint**: `GET /orders`

**설명**: 사용자의 주문 내역을 조회합니다 (최근 30일).

**인증**: 필요 (USER)

**요청**: 없음

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "orderId": 123,
        "menuName": "아메리카노",
        "price": 4500,
        "orderTime": "2026-05-06T10:30:00"
      },
      {
        "orderId": 122,
        "menuName": "카페라떼",
        "price": 5000,
        "orderTime": "2026-05-05T14:20:00"
      }
    ]
  },
  "message": "주문 내역 조회 성공"
}
```

**에러**: 없음 (빈 배열 반환 가능)

---

## 7. 관리자 API

### 7.1 관리자 로그인

**Endpoint**: `POST /admin/auth/login`

**설명**: 관리자 계정으로 로그인합니다.

**인증**: 불필요

**요청**:
```json
{
  "email": "admin@example.com",
  "password": "AdminPass123"
}
```

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "userId": 10,
    "email": "admin@example.com",
    "name": "관리자",
    "role": "ADMIN",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "관리자 로그인 성공"
}
```

**에러**:
- `ADMIN_001` (403): 관리자 권한이 없습니다
- `ADMIN_002` (401): 이메일 또는 비밀번호가 일치하지 않습니다

---

### 7.2 메뉴 등록

**Endpoint**: `POST /admin/menus`

**설명**: 새로운 메뉴를 등록합니다.

**인증**: 필요 (ADMIN)

**요청**:
```json
{
  "name": "아메리카노",
  "price": 4500,
  "description": "깊고 진한 에스프레소",
  "ingredients": ["원두", "물"]
}
```

**응답** (201 Created):
```json
{
  "success": true,
  "data": {
    "menuId": 1,
    "name": "아메리카노",
    "price": 4500,
    "status": "AVAILABLE"
  },
  "message": "메뉴 등록 성공"
}
```

**에러**:
- `MENU_001` (409): 이미 존재하는 메뉴명입니다
- `MENU_002` (400): 가격은 100원 ~ 100,000원 사이여야 합니다
- `MENU_003` (403): 관리자 권한이 필요합니다

---

### 7.3 메뉴 수정

**Endpoint**: `PATCH /admin/menus/{menuId}`

**설명**: 기존 메뉴 정보를 수정합니다.

**인증**: 필요 (ADMIN)

**요청**:
```json
{
  "name": "아메리카노 (Large)",
  "price": 5000,
  "description": "더 큰 사이즈",
  "ingredients": ["원두", "물"]
}
```

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "menuId": 1,
    "name": "아메리카노 (Large)",
    "price": 5000
  },
  "message": "메뉴 수정 성공"
}
```

**에러**:
- `MENU_004` (404): 존재하지 않는 메뉴입니다
- `MENU_003` (403): 관리자 권한이 필요합니다

---

### 7.4 메뉴 상태 변경

**Endpoint**: `PATCH /admin/menus/{menuId}/status`

**설명**: 메뉴 상태를 변경합니다 (품절/판매중).

**인증**: 필요 (ADMIN)

**요청**:
```json
{
  "status": "SOLD_OUT"
}
```

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "menuId": 1,
    "name": "아메리카노",
    "status": "SOLD_OUT"
  },
  "message": "메뉴 상태 변경 성공"
}
```

**에러**:
- `MENU_004` (404): 존재하지 않는 메뉴입니다
- `MENU_005` (400): 유효하지 않은 상태값입니다
- `MENU_003` (403): 관리자 권한이 필요합니다

---

### 7.5 메뉴 삭제

**Endpoint**: `DELETE /admin/menus/{menuId}`

**설명**: 메뉴를 삭제합니다 (논리 삭제).

**인증**: 필요 (ADMIN)

**요청**: 없음

**응답** (200 OK):
```json
{
  "success": true,
  "data": {
    "menuId": 1
  },
  "message": "메뉴 삭제 성공"
}
```

**에러**:
- `MENU_004` (404): 존재하지 않는 메뉴입니다
- `MENU_003` (403): 관리자 권한이 필요합니다

---

## 8. API 목록 요약

| 카테고리 | Method | Endpoint | 인증 | 설명 |
|----------|--------|----------|------|------|
| **인증** | POST | /auth/signup | ❌ | 회원가입 |
| **인증** | POST | /auth/login | ❌ | 로그인 |
| **인증** | POST | /auth/refresh | ❌ | 토큰 갱신 |
| **메뉴** | GET | /menus | ✅ USER | 전체 메뉴 조회 |
| **메뉴** | GET | /menus/popular | ✅ USER | 인기 메뉴 조회 |
| **포인트** | POST | /points/charge | ✅ USER | 포인트 충전 |
| **포인트** | GET | /points | ✅ USER | 포인트 조회 |
| **주문** | POST | /orders | ✅ USER | 주문 생성 |
| **주문** | GET | /orders | ✅ USER | 주문 내역 조회 |
| **관리자** | POST | /admin/auth/login | ❌ | 관리자 로그인 |
| **관리자** | POST | /admin/menus | ✅ ADMIN | 메뉴 등록 |
| **관리자** | PATCH | /admin/menus/{menuId} | ✅ ADMIN | 메뉴 수정 |
| **관리자** | PATCH | /admin/menus/{menuId}/status | ✅ ADMIN | 메뉴 상태 변경 |
| **관리자** | DELETE | /admin/menus/{menuId} | ✅ ADMIN | 메뉴 삭제 |

---

## 9. OpenAPI (Swagger) 명세

```yaml
openapi: 3.0.3
info:
  title: 커피숍 주문 시스템 API
  description: |
    다중 서버 환경에서 안정적으로 동작하는 커피숍 주문 시스템
    
    **참고**: 이 문서는 주요 엔드포인트 예시만 포함합니다.
    전체 API 스펙은 실제 구현 시 Swagger UI에서 자동 생성됩니다.
    
    **포함된 예시**:
    - 인증 API (회원가입, 로그인)
    - 메뉴 조회 API
    - 주문 생성 API
    
    **전체 14개 엔드포인트**는 Spring Boot + Springdoc OpenAPI로 자동 생성
  version: 1.0.0
  contact:
    name: API Support
    email: support@example.com

servers:
  - url: http://localhost:8080/api/v1
    description: 로컬 개발 서버

tags:
  - name: Auth
    description: 인증 관련 API
  - name: Menu
    description: 메뉴 관련 API
  - name: Point
    description: 포인트 관련 API
  - name: Order
    description: 주문 관련 API
  - name: Admin
    description: 관리자 관련 API

components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    User:
      type: object
      properties:
        userId:
          type: integer
          format: int64
        email:
          type: string
          format: email
        name:
          type: string

    Menu:
      type: object
      properties:
        menuId:
          type: integer
          format: int64
        name:
          type: string
        price:
          type: integer
        description:
          type: string
        status:
          type: string
          enum: [AVAILABLE, SOLD_OUT]

    Order:
      type: object
      properties:
        orderId:
          type: integer
          format: int64
        menuName:
          type: string
        price:
          type: integer
        orderTime:
          type: string
          format: date-time

    Error:
      type: object
      properties:
        success:
          type: boolean
          example: false
        error:
          type: object
          properties:
            code:
              type: string
            message:
              type: string

paths:
  /auth/signup:
    post:
      tags:
        - Auth
      summary: 회원가입
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - email
                - password
                - name
              properties:
                email:
                  type: string
                  format: email
                password:
                  type: string
                  minLength: 8
                name:
                  type: string
                  minLength: 2
      responses:
        '201':
          description: 회원가입 성공
        '409':
          description: 이메일 중복

  /auth/login:
    post:
      tags:
        - Auth
      summary: 로그인
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - email
                - password
              properties:
                email:
                  type: string
                password:
                  type: string
      responses:
        '200':
          description: 로그인 성공
        '401':
          description: 인증 실패

  /menus:
    get:
      tags:
        - Menu
      summary: 전체 메뉴 조회
      security:
        - BearerAuth: []
      responses:
        '200':
          description: 메뉴 조회 성공
          content:
            application/json:
              schema:
                type: object
                properties:
                  success:
                    type: boolean
                  data:
                    type: object
                    properties:
                      menus:
                        type: array
                        items:
                          $ref: '#/components/schemas/Menu'

  /orders:
    post:
      tags:
        - Order
      summary: 주문 생성
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - menuId
              properties:
                menuId:
                  type: integer
                  format: int64
      responses:
        '201':
          description: 주문 성공
        '400':
          description: 잘못된 요청
        '429':
          description: 요청 과다
```

---

## 10. 다음 단계

1. ✅ 프로젝트 개요서
2. ✅ 사용자 시나리오
3. ✅ 유스케이스 명세서
4. ✅ 기능 명세서
5. ✅ ERD 설계서
6. ✅ **API 명세서**
7. ⏭️ 화면 설계서
8. ⏭️ 인프라 아키텍처 다이어그램
9. ⏭️ 동시성 제어 설계서
10. ⏭️ ADR

---

## 문서 이력

| 버전 | 작성일 | 작성자 | 변경 내역 |
|------|--------|--------|-----------|
| 1.0 | 2026-05-06 | Kiro | 초안 작성 |
