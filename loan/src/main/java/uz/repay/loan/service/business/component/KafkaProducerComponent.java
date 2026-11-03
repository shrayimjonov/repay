package uz.repay.loan.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.service.broker.KafkaProducer;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerComponent {
    private final KafkaProducer kafkaProducer;

    public SimpleResponse<CompletableFuture<?>> send(String topic, String data) {
        log.info("Sending data to broker to topic: {} with data: {}", topic, data);
        return kafkaProducer.send(topic, data);
    }
}
