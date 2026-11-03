package uz.repay.card.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.service.broker.KafkaProducer;
import uz.repay.common.config.kafka.Topics;
import uz.repay.common.model.dto.request.kafka.CardConfirmationEventDTO;
import uz.repay.common.model.enums.Language;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationComponent {
    private final KafkaProducer kafkaProducer;

    @Async
    public void sendCardConfirmNotification(String loanExtId, String code, String card, Language language, String phone) {
        log.info("Send notification component take params: {} {} {}",
                loanExtId, "{code}", card);


        var cardConfirmationEventDTO = new CardConfirmationEventDTO(
                loanExtId,
                code,
                "**** " + card,
                language,
                phone
        );
        kafkaProducer.send(Topics.CARD_CONFIRMATION, cardConfirmationEventDTO.toJson());
        log.info("Send notification finished!");
    }
}
