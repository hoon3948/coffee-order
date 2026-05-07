package kr.spartaclub.coffeeorder.config;

import static org.mockito.Mockito.mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 테스트용 설정
 */
@TestConfiguration
public class TestConfig {

    /**
     * 테스트용 Mock KafkaTemplate
     */
    @Bean
    @Primary
    public KafkaTemplate<String, String> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}
