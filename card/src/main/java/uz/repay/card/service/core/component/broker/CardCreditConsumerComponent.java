package uz.repay.card.service.core.component.broker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.service.RecordToClassConverter;
import uz.repay.card.model.dto.broker.event.NewLoanEventDTO;
import uz.repay.card.service.business.CardBalanceAlertService;
import uz.repay.common.config.kafka.Topics;
import uz.repay.common.model.dto.request.kafka.CardCreditEventDTO;


@Component
@Slf4j
@RequiredArgsConstructor
public class CardCreditConsumerComponent {
    private final CardBalanceAlertService cardBalanceAlertService;
    private final RecordToClassConverter recordToClassConverter;


    @KafkaListener(topics = Topics.CARD_CREDIT_EVENT_TOPIC, groupId = "card-credit-event-davr-mobile")
    public void listen(final ConsumerRecord<String, String> record) {
        log.info("Received command to push card balance from Davr Mobile with request: {}", record);

        var convertResult = recordToClassConverter.convertMessageV2(record.value(), CardCreditEventDTO.class);

        if (!convertResult.getStatus()) {
            log.error("Convert result is unsuccessful and will be returned");
            return;
        }

        var eventDTO = convertResult.getData();

        cardBalanceAlertService.alertedFromDavrMobile(eventDTO);

        log.info("Finished command to push card balance");
    }
}
