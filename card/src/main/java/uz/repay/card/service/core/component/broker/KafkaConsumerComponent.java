package uz.repay.card.service.core.component.broker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.service.RecordToClassConverter;
import uz.repay.card.model.dto.broker.event.NewLoanEventDTO;
import uz.repay.card.service.business.component.CardFinderComponent;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.common.config.kafka.Topics;


@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerComponent {
    private final RecordToClassConverter recordToClassConverter;
    private final CardFinderComponent cardFinderComponent;
    private final LoanDAO loanDAO;

    @KafkaListener(topics = Topics.CARD_DEBEZIUM_NEW_LOAN_CARD_FINDER, groupId = "card-debezium-new-loan")
    public void firstTimeCardFinder(ConsumerRecord<String, String> record) {
        log.info("Received first time card finder debezium event: {}", record);

        var convertResult = recordToClassConverter.convertMessage(record.value(), NewLoanEventDTO.class);


        if (!convertResult.getStatus()) {
            log.error("Convert result is unsuccessful and will be returned");
            return;
        }

        var newLoanDTO = convertResult.getData();

        if (newLoanDTO.getAfter() == null || newLoanDTO.getAfter().getExternalId() == null) {
            log.error("Necessary fields are null! Event DTO: {}", newLoanDTO);
            return;
        }

        var loanExtId = newLoanDTO.getAfter().getExternalId();

        if (loanExtId.isEmpty()) {
            log.error("End find card method, cause: loanExtId is null or empty");
            return;
        }

        var loanResult = loanDAO.findLoanOptionalByExtId(loanExtId);

        if (loanResult.isEmpty()) {
            log.error("End find card method, cause: loanExtId: {} is not found", loanExtId);
            return;
        }

        var loan = loanResult.get();

        cardFinderComponent.findCardsFromFreeDetectors(loan);

        log.info("Finished first time card finder debezium event");
    }
}
