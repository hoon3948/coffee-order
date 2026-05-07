package kr.spartaclub.coffeeorder.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka 설정
 * Kafka Topic 생성 및 설정
 */
@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.point-charged}")
    private String pointChargedTopic;

    @Value("${kafka.topics.point-used}")
    private String pointUsedTopic;

    /**
     * 주문 생성 이벤트 토픽
     * @return NewTopic
     */
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(orderCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * 포인트 충전 이벤트 토픽
     * @return NewTopic
     */
    @Bean
    public NewTopic pointChargedTopic() {
        return TopicBuilder.name(pointChargedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * 포인트 사용 이벤트 토픽
     * @return NewTopic
     */
    @Bean
    public NewTopic pointUsedTopic() {
        return TopicBuilder.name(pointUsedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
