package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.mongo.entity.CardData;
import uz.repay.card.mongo.repository.CardDataRepository;
import uz.repay.card.service.core.CardDataMongoDAO;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CardDataMongoDAOImpl implements CardDataMongoDAO {
    private final CardDataRepository repository;

    @Override
    public void save(CardData cardData) {
        log.info("Save card data to mongo: {}", cardData);
        repository.save(cardData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardData> getAllByCardId(Long id) {
        log.info("Get card data list by card id: {}", id);
        return repository.findByCardId(id);
    }
}
