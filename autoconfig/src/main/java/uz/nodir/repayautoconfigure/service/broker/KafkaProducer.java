package uz.nodir.repayautoconfigure.service.broker;

import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.concurrent.CompletableFuture;

public interface KafkaProducer {
    <T> SimpleResponse<CompletableFuture<?>> send(String topic, T data);

    <T> SimpleResponse<CompletableFuture<?>> sendWithKey(String topic, String key, T record);

    <T> SimpleResponse<CompletableFuture<?>> sendWithKeyToPartition(String topic, Integer partition, String key, T record);
}
