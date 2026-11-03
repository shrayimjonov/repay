package uz.repay.card.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.service.broker.KafkaProducer;
import uz.repay.card.model.dto.adapter.CardBalanceFetchRequest;
import uz.repay.card.model.dto.backlash.CardDeactivationDTO;
import uz.repay.common.config.kafka.Topics;
import uz.repay.common.model.dto.response.core.CoreError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class DisableCardsComponent {
    private final KafkaProducer kafkaProducer;

    public void disableNotFoundCards(List<CardBalanceFetchRequest> cards, List<String> resultTokensList) {
        var notFoundList = new ArrayList<Long>();
        var returnedTokens = resultTokensList == null ? List.<String>of() : resultTokensList;

        cards
                .forEach(c -> {
                            if (returnedTokens
                                    .stream()
                                    .noneMatch(
                                            r -> Objects.equals(c.getCard(), r)
                                    )) {
                                notFoundList.add(c.getCardId());

                                kafkaProducer.send(Topics.CARD_DEACTIVATE_TOPIC, new CardDeactivationDTO(c.getCardId()));
                            }
                        }
                );

        if (!notFoundList.isEmpty())
            log.warn("Not found cards from response card balance: count={}, cardIds={}, requestedCards={}, resultTokens={}",
                    notFoundList.size(), notFoundList, summarizeRequests(cards), summarizeSecureValues(returnedTokens));
    }

    public void ifTokensNotFoundSetNotActive(List<CardBalanceFetchRequest> cardList, CoreError coreError) {
        log.warn("Taken error from response card balance: errorCode={}, errorMessage={}, cards={}",
                coreError.getCode(), coreError.getMessage(), summarizeRequests(cardList));

        if (coreError.getCode().equals(-257)) {
            cardList
                    .forEach(
                            c -> {
                                kafkaProducer.send(Topics.CARD_DEACTIVATE_TOPIC, new CardDeactivationDTO(c.getCardId()));
                            }
                    );

        }
    }

    public void setNotActiveCardAndSave(Long key) {
        log.info("Card with id: {} returned \"Card not found\" message in response and will be set not active card", key);
        kafkaProducer.send(Topics.CARD_DEACTIVATE_TOPIC, new CardDeactivationDTO(key));
    }

    private static String summarizeRequests(List<CardBalanceFetchRequest> cards) {
        if (cards == null || cards.isEmpty())
            return "[]";

        return cards
                .stream()
                .limit(10)
                .map(c -> "{cardId=%s, processing=%s, type=%s, token=%s}"
                        .formatted(c.getCardId(), c.getProcessing(), c.getType(), summarizeSecureValue(c.getCard())))
                .toList()
                .toString();
    }

    private static String summarizeSecureValues(List<String> values) {
        if (values == null || values.isEmpty())
            return "[]";

        return values
                .stream()
                .limit(10)
                .map(DisableCardsComponent::summarizeSecureValue)
                .toList()
                .toString();
    }

    private static String summarizeSecureValue(String value) {
        if (value == null)
            return null;

        var lastChars = Math.min(6, value.length());
        return "len=%d,last=%s".formatted(value.length(), value.substring(value.length() - lastChars));
    }

}
