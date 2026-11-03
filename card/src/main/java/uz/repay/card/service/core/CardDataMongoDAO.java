package uz.repay.card.service.core;

import uz.repay.card.mongo.entity.CardData;

import java.util.List;

public interface CardDataMongoDAO {

    void save(CardData cardData);

    List<CardData> getAllByCardId(Long id);
}
