package kr.spartaclub.coffeeorder.global.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Web MVC 설정
 * - HTTP 메시지 컨버터 UTF-8 인코딩 설정
 * - Jackson ObjectMapper 설정
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @PostConstruct
    public void init() {
        log.info("WebConfig 초기화 완료 - UTF-8 인코딩 설정 적용");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("HTTP 메시지 컨버터 설정 중 - UTF-8 인코딩 적용");
        
        // String 메시지 컨버터 UTF-8 설정
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setWriteAcceptCharset(false); // Accept-Charset 헤더 제거
        converters.add(stringConverter);

        // Jackson JSON 메시지 컨버터 UTF-8 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식으로 날짜 출력
        
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(jsonConverter);
        
        log.info("HTTP 메시지 컨버터 설정 완료 - {} 개의 컨버터 등록", converters.size());
    }
}
