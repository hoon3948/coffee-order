package kr.spartaclub.coffeeorder.global.common.exception;

import kr.spartaclub.coffeeorder.global.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("비즈니스 예외 발생: code={}, message={}", errorCode.getCode(), ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), ex.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    /**
     * Validation 예외 처리 (@Valid, @Validated)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation 실패: {}", errors);
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_001.getCode(),
                ErrorCode.COMMON_001.getMessage(),
                errors
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 인증 예외 처리 (Spring Security)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex
    ) {
        log.warn("인증 실패: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.AUTH_005.getCode(),
                ErrorCode.AUTH_005.getMessage()
        );
        
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * 권한 예외 처리 (Spring Security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex
    ) {
        log.warn("권한 없음: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.ADMIN_001.getCode(),
                ErrorCode.ADMIN_001.getMessage()
        );
        
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {
        log.warn("잘못된 요청: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_001.getCode(),
                ex.getMessage()
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 지원하지 않는 HTTP 메서드 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex
    ) {
        log.warn("지원하지 않는 HTTP 메서드: {}", ex.getMethod());
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_003.getCode(),
                ErrorCode.COMMON_003.getMessage()
        );
        
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(response);
    }

    /**
     * 지원하지 않는 미디어 타입 예외 처리
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex
    ) {
        log.warn("지원하지 않는 미디어 타입: {}", ex.getContentType());
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_004.getCode(),
                ErrorCode.COMMON_004.getMessage()
        );
        
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(response);
    }

    /**
     * 요청 파라미터 누락 예외 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(
            MissingServletRequestParameterException ex
    ) {
        log.warn("필수 파라미터 누락: {}", ex.getParameterName());
        
        String message = String.format("필수 파라미터가 누락되었습니다: %s", ex.getParameterName());
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_001.getCode(),
                message
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex
    ) {
        log.warn("타입 불일치: parameter={}, value={}", ex.getName(), ex.getValue());
        
        String message = String.format("파라미터 타입이 올바르지 않습니다: %s", ex.getName());
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_001.getCode(),
                message
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * JSON 파싱 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(
            HttpMessageNotReadableException ex
    ) {
        log.warn("JSON 파싱 실패: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_001.getCode(),
                "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요."
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 일반 예외 처리 (최종 fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("예상치 못한 서버 오류 발생", ex);
        
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_002.getCode(),
                ErrorCode.COMMON_002.getMessage()
        );
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
