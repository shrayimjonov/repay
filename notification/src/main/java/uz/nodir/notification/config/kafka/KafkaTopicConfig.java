package uz.nodir.notification.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.List;

import static uz.repay.common.config.kafka.Topics.*;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    public final static List<String> topics = List.of(
            PAY_NOTIFICATION,
            SMS_DEBT
    );

    @Bean
    public List<NewTopic> createTopics() {
        log.info("Build topics for: {}", topics);
        return topics
                .stream()
                .map(
                        t -> TopicBuilder
                                .name(t)
                                .build()
                )
                .toList();

    }


}
