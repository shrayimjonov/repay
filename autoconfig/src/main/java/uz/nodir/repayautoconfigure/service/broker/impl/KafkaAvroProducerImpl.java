package uz.nodir.repayautoconfigure.service.broker.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.configuration.broker.condition.AvroBrokerCondition;
import uz.nodir.repayautoconfigure.service.broker.KafkaProducer;
import uz.nodir.repayautoconfigure.utils.BrokerUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
@RequiredArgsConstructor
@Conditional(AvroBrokerCondition.class)
public class KafkaAvroProducerImpl implements KafkaProducer {
    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;


    @Override
    public <T> SimpleResponse<CompletableFuture<?>> send(String topic, T data) {
        log.info("Sending data to topic: {} with data: {}", topic, data.toString());

        var recordResult = BrokerUtils.check(data, SpecificRecord.class);

        if (!recordResult.getStatus())
            return new SimpleResponse<>(recordResult.getError().getCode(), recordResult.getError().getMessage());

        var result = kafkaTemplate.send(topic, recordResult.getData());
        return new SimpleResponse<>(result);
    }

    @Override
    public <T> SimpleResponse<CompletableFuture<?>> sendWithKey(String topic, String key, T data) {
        log.info("Sending data with key: {} to topic: {} with data: {}", key, topic, data.toString());

        var recordResult = BrokerUtils.check(data, SpecificRecord.class);

        if (!recordResult.getStatus())
            return new SimpleResponse<>(recordResult.getError().getCode(), recordResult.getError().getMessage());

        var result = kafkaTemplate.send(topic, key, recordResult.getData());
        return new SimpleResponse<>(result);
    }

    @Override
    public <T> SimpleResponse<CompletableFuture<?>> sendWithKeyToPartition(String topic, Integer partition, String key, T data) {
        log.info("Sending data with key: {} to topic: {} to partition: {} with data: {}", key, topic, partition, data.toString());

        var recordResult = BrokerUtils.check(data, SpecificRecord.class);

        if (!recordResult.getStatus())
            return new SimpleResponse<>(recordResult.getError().getCode(), recordResult.getError().getMessage());


        var result = kafkaTemplate.send(topic, partition, key, recordResult.getData());
        return new SimpleResponse<>(result);
    }
}
