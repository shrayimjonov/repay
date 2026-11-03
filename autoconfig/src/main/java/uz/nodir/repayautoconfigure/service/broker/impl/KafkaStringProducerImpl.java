package uz.nodir.repayautoconfigure.service.broker.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.configuration.broker.condition.DefaultBrokerCondition;
import uz.nodir.repayautoconfigure.service.broker.KafkaProducer;
import uz.nodir.repayautoconfigure.utils.BrokerUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
@Conditional(DefaultBrokerCondition.class)
public class KafkaStringProducerImpl implements KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public <T> SimpleResponse<CompletableFuture<?>> send(String topic, T data) {
        log.info("Sending data to broker to topic: {} with data: {}", topic, data.toString());

        var recordResult = BrokerUtils.check(data, String.class);

        if (!recordResult.getStatus())
            return new SimpleResponse<>(recordResult.getError().getCode(), recordResult.getError().getMessage());

        var result = kafkaTemplate.send(topic, recordResult.getData());
        return new SimpleResponse<>(result);
    }


    @Override
    public <T> SimpleResponse<CompletableFuture<?>> sendWithKey(String topic, String key, T data) {
        log.info("Sending data with key: {} to topic: {} with data: {}", key, topic, data.toString());

        var recordResult = BrokerUtils.check(data, String.class);

        if (!recordResult.getStatus())
            return new SimpleResponse<>(recordResult.getError().getCode(), recordResult.getError().getMessage());

        var result = kafkaTemplate.send(topic, key, recordResult.getData());
        return new SimpleResponse<>(result);
    }

    @Override
    public <T> SimpleResponse<CompletableFuture<?>> sendWithKeyToPartition(String topic, Integer partition, String key, T data) {
        log.info("Sending data with key: {} to topic: {} to partition: {} with data: {}", key, topic, partition, data.toString());

        var recordResult = BrokerUtils.check(data, String.class);

        if (!recordResult.getStatus())
            return new SimpleResponse<>(recordResult.getError().getCode(), recordResult.getError().getMessage());


        var result = kafkaTemplate.send(topic, partition, key, recordResult.getData());
        return new SimpleResponse<>(result);
    }
}
