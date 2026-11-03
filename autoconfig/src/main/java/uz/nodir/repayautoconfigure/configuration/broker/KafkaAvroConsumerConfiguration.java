package uz.nodir.repayautoconfigure.configuration.broker;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import uz.nodir.repayautoconfigure.configuration.broker.condition.AvroBrokerCondition;
import uz.nodir.repayautoconfigure.model.property.SchemaRegistryProperty;

import java.util.HashMap;
import java.util.Map;


@Configuration
@Slf4j
@Conditional(AvroBrokerCondition.class)
@ConditionalOnProperty(prefix = "spring.kafka", name = "bootstrap-servers", matchIfMissing = false)
@EnableConfigurationProperties(value = SchemaRegistryProperty.class)
public class KafkaAvroConsumerConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.application.name}")
    private String applicationName;

    private final SchemaRegistryProperty schemaRegistryProperty;

    public KafkaAvroConsumerConfiguration(SchemaRegistryProperty schemaRegistryProperty) {
        this.schemaRegistryProperty = schemaRegistryProperty;
    }

    @PostConstruct
    public void checkRegistry() {
        String url = schemaRegistryProperty.getUrl();
        log.info("Schema registry URL: {}", url);

        if (url == null || url.isEmpty() || url.isBlank()) {
            throw new RuntimeException("Schema registry URL is empty");
        }
    }

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, applicationName);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryProperty.getUrl());
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);
        log.info("Kafka consumer configuration with properties: {}", props);
        return props;
    }


    @Bean
    public ConsumerFactory<String, SpecificRecord> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SpecificRecord>> factory(ConsumerFactory<String, SpecificRecord> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        return factory;
    }
}
