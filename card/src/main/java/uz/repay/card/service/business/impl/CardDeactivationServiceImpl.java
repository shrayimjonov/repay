package uz.repay.card.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.model.entity.Card;
import uz.repay.card.mongo.entity.CardData;
import uz.repay.card.service.business.CardDeactivationService;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.CardDataMongoDAO;
import uz.repay.common.utils.DateUtils;

import java.util.Comparator;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardDeactivationServiceImpl implements CardDeactivationService {
    private final CardDataMongoDAO cardDataMongoDAO;
    private final CardDAO cardDAO;

    @Override
    @Transactional
    public void checkCardToDeactivate(Long id) {
        log.info("Check card from card data by id: {}", id);

        var cardDataList = cardDataMongoDAO.getAllByCardId(id);
        log.info("card data list size: {}", cardDataList.size());

        var card = cardDAO.findById(id);

        if (cardDataList.isEmpty()) {
            saveCardData(id, card);
            log.info("Card checked from card data by :{} and finished", card.getMaskedPan());
            return;
        }

        cardDataList.sort(Comparator.comparing(CardData::getCreatedTime));

        var lastCardData = cardDataList.getLast();

        log.info("Last card data entity: {}", lastCardData);
        if (cardDataList.size() < 5) {
            if (!DateUtils.getDateNow().equals(lastCardData.getCreatedTime().toLocalDate()))
                saveCardData(id, card);
            else
                log.info("Today card: {} is already added to card data list.", card.getMaskedPan());
        } else {
            card.setIsActive(false);
            cardDAO.update(card);
        }

        log.info("Card checked from card data by :{} and finished", card.getMaskedPan());
    }

    private void saveCardData(Long id, Card card) {

        var cardData = new CardData(
                UUID.randomUUID(),
                DateUtils.getDateTimeNow(),
                id,
                card.getMaskedPan(),
                card.getHolder(),
                card.getToken()
        );

        cardDataMongoDAO.save(cardData);
    }
}
