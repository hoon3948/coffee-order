package kr.spartaclub.coffeeorder.global.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * 모든 REST API 응답에 UTF-8 인코딩을 강제 적용
 */
@Slf4j
@RestControllerAdvice
public class Utf8EncodingAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                   Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                   ServerHttpRequest request, ServerHttpResponse response) {
        
        // Content-Type 헤더에 charset=UTF-8 강제 설정
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        log.debug("UTF-8 인코딩 적용: {}", request.getURI());
        
        return body;
    }
}
