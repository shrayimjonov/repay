package uz.repay.card.service.core.component.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.autopay.request.AutoPayPushRequestDTO;
import uz.repay.card.service.business.CardBalanceAlertService;
import uz.repay.card.util.KafkaUtils;

import java.time.LocalDateTime;

/**
 * Powered by: Shukhratjon Rayimjonov
 * Date: 22.06.2026
 */

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KafkaCardAlertConsumer {

    ObjectMapper objectMapper;
    CardBalanceAlertService cardBalanceAlertService;


    @KafkaListener(
            topics = KafkaUtils.CARD_ALERT_TOPIC,
            groupId = "card-balance-alert-consumer-group",
            concurrency = "1"
    )
    public void consume(String message) {
        try {
            AutoPayPushRequestDTO request = objectMapper.readValue(message, AutoPayPushRequestDTO.class);
            cardBalanceAlertService.pushToLoansWithAlertedCard(request, LocalDateTime.now());
        } catch (JsonProcessingException e) {
            log.info("Object reading error: {}", e.getMessage());
        }
    }
}