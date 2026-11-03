package uz.repay.payment.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class KafkaProducerComponent {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SimpleResponse<CompletableFuture<SendResult<String, String>>> send(String topic, String data) {
        log.info("Sending data to broker to topic: {} with data: {}", topic, data);
        try {
            var result = kafkaTemplate.send(topic, data);
            return new SimpleResponse<>(result);
        } catch (Exception e) {
            var message = "Occurred an error while sending message to broker with message: %s ".formatted(e.getMessage());
            log.error(message);
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), message);
        }
    }

}
