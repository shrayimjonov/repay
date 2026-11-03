package uz.nodir.repayautoconfigure.configuration.broker;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import uz.nodir.repayautoconfigure.configuration.broker.condition.AvroBrokerCondition;
import uz.nodir.repayautoconfigure.model.property.SchemaRegistryProperty;

import java.util.HashMap;
import java.util.Map;


@Configuration
@Slf4j
@Conditional(AvroBrokerCondition.class)
@ConditionalOnProperty(prefix = "spring.kafka", name = "bootstrap-servers", matchIfMissing = false)
@EnableConfigurationProperties(value = SchemaRegistryProperty.class)
public class KafkaAvroProducerConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private final SchemaRegistryProperty schemaRegistryProperty;

    public KafkaAvroProducerConfiguration(SchemaRegistryProperty schemaRegistryProperty) {
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

    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryProperty.getUrl());
        props.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, false);
        props.put(KafkaAvroSerializerConfig.USE_LATEST_VERSION, true);
        props.put(KafkaAvroSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY, "io.confluent.kafka.serializers.subject.RecordNameStrategy");
        log.info("Kafka producer configuration with properties: {}", props);
        return props;
    }


    @Bean
    public ProducerFactory<String, SpecificRecord> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, SpecificRecord> kafkaTemplate(ProducerFactory<String, SpecificRecord> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
