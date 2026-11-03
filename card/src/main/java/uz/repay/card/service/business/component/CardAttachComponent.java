package uz.repay.card.service.business.component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.service.broker.KafkaProducer;
import uz.repay.common.config.kafka.Topics;
import uz.repay.common.model.dto.request.kafka.CardAttachedEventDTO;


@Component
@Slf4j
@RequiredArgsConstructor
public class CardAttachComponent {
    private final KafkaProducer kafkaProducer;

    @EventListener
    public void cardAttachedNotification(@NonNull CardAttachedEventDTO eventDTO) {
        log.info("Begin card attached event listener with request: {}", eventDTO);
        kafkaProducer.send(Topics.NOTIFICATION_CARD_ATTACHED, eventDTO.toJson());
    }
}
