package kr.spartaclub.coffeeorder.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 에러 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;
    private ErrorDetail error;
    private LocalDateTime timestamp;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ErrorDetail {
        private String code;
        private String message;
        private Map<String, String> details; // Validation 에러 상세 정보
    }

    /**
     * 에러 응답 생성 (코드 + 메시지)
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .success(false)
                .error(ErrorDetail.builder()
                        .code(code)
                        .message(message)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 에러 응답 생성 (코드 + 메시지 + 상세)
     */
    public static ErrorResponse of(String code, String message, Map<String, String> details) {
        return ErrorResponse.builder()
                .success(false)
                .error(ErrorDetail.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
