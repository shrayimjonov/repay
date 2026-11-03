package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.model.dto.core.request.card.OTPRequestDTO;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humo.response.HumoTokenResponse;
import uz.repay.card.model.dto.master.response.MasterBalanceResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.entity.Card;
import uz.repay.card.repository.CardRepository;
import uz.repay.card.repository.projection.HumoAutoDebitCardProjection;
import uz.repay.card.repository.projection.HumoLoanCardProjection;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.CardUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static uz.repay.card.util.ServiceUtils.improvePhone;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardDAOImpl implements CardDAO {
    private final CardRepository repository;
    private final HuMoClientService huMoClientService;
    private final RedissonClient redissonClient;
    private final static Integer LOCK_WAIT_TIME = 20;
    private final static Integer LOCK_TTL = 30;


    @Override
    @Transactional
    @Deprecated
    public synchronized void create(OTPRequestDTO requestDTO, Processing processing) {
        var cardOptional = findCardByPan(CardUtils.encryptPan(requestDTO.getCard()));

        if (cardOptional.isPresent()) {
            log.info("Card: {} already has in Database", requestDTO.getCard());
            return;
        }

        log.info("Card is not found DB and will be created..");

        var preparedEntity = new Card(requestDTO.getCard(), requestDTO.getExpiry(), processing);
        log.info("Prepared card entity: {}", preparedEntity);

        repository.save(preparedEntity);

    }

    @Override
    @Transactional
    public synchronized Card create(HumoCardInfoResponse response, Boolean isConfirmed) {
        var cardOptional = findCardByPan(CardUtils.encryptPan(response.result().card().primaryAccountNumber()));

        if (cardOptional.isPresent()) {
            log.info("Card: {} already has in Database", response.result().card().primaryAccountNumber());
            var card = cardOptional.get();

            card.setHolder(response.result().card().nameOnCard());
            card.setExpiry(response.result().card().expiry());

            if (response.result().mb() != null && response.result().mb().phone() != null)
                card.setPhone(response.result().mb().phone());


            return repository.save(card);
        }

        String phone = validatePhone(response);

        String token = getHumoCardToken(response);

        var preparedEntity = new Card(
                response.result().card().primaryAccountNumber(),
                response.result().card().expiry(),
                response.result().card().nameOnCard(),
                Processing.HUMO,
                phone,
                token,
                isConfirmed,
                Currency.UZS
        );

        log.info("Prepared HUMO card entity 4 saving: {}", preparedEntity);
        return repository.save(preparedEntity);
    }

    @Override
    @Transactional
    public synchronized Card create(CardBaseResult result, Boolean isConfirmed, Processing pc) {
        var cardOptional = findCardByToken(result.getId());

        if (cardOptional.isPresent()) {
            var card = cardOptional.get();

            card.setToken(result.getId());
            card.setHolder(result.getFullName());
            card.setPhone(result.getPhone());
            card.setProcessing(pc);

            return repository.save(card);
        }

        var currency = CardUtils.isSumPC(pc) ? Currency.UZS : Currency.USD;

        var preparedEntity = new Card(
                result.getPan(),
                result.getExpiry(),
                result.getFullName(),
                pc,
                result.getPhone(),
                result.getId(),
                isConfirmed,
                currency
        );

        log.info("Creating new card entity: {}", preparedEntity);

        return repository.save(preparedEntity);
    }


    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public Optional<Card> findCardByPan(String card) {
        log.info("Search card by pan: {} ", card);
        return repository.findCardByCard(card);
    }


    @Override
    @Transactional
    public Optional<Card> findCardByToken(String token) {
        log.info("Find card by token: {}", token);
        var result = repository.findCardByToken(token);

        log.info("Found card by token result isPresent: {}", result.isPresent());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Card> findCardByPanV2(String card) {
        log.info("Search card by pan: {} ", card);
        return repository.findCardByCard(card);
    }

    @Override
    @Transactional
    public synchronized Card create(Card preparedCard) {
        log.info("Create new card: {}", preparedCard);

        RLock lock = redissonClient.getLock("card-save:" + preparedCard.getToken());

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_TTL, TimeUnit.SECONDS);

            if (!isLocked)
                throw new GeneralErrorException("Card is locked: %s".formatted(preparedCard.getToken()));

            var cardOptional = findCardByToken(preparedCard.getToken());

            if (cardOptional.isPresent()) {
                log.info("Card: {} already has and will be returned entity", cardOptional.get().getPan());

                var savedCard = cardOptional.get();

                if (savedCard.getPhone() == null || savedCard.getHolder() == null
                        || !savedCard.getIsConfirmed()
                ) {
                    savedCard.setPhone(preparedCard.getPhone());
                    savedCard.setHolder(preparedCard.getHolder());
                    savedCard.setIsConfirmed(true);
                    savedCard.setProcessing(preparedCard.getProcessing());
                    repository.save(savedCard);
                }

                return cardOptional.get();
            }

            log.info("Creating new card entity: {}", preparedCard);

            return repository.save(preparedCard);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GeneralErrorException("Ошибка блокировки: %s".formatted(e.getMessage()));
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock(); // Освобождаем лок
            }
        }
    }


    @Override
    @Transactional
    public void update(Card card) {
        repository.save(card);
    }

    @Override
    @Transactional(readOnly = true)
    public Card findById(Long id) {
        log.info("Card info by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card with id: %d not found in database!".formatted(id)));
    }

    private HumoTokenResponse getHumoTokenResponse(String card) {
        log.info("Send token request for pan: {}", CardUtils.maskPanDigits(card));
        try {
            var response = huMoClientService.getCardToken(card);
            log.info("Token response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Token response has error: {}", e.getMessage());
            return null;
        }
    }


    private String getHumoCardToken(HumoCardInfoResponse response) {
        var tokenResponse = getHumoTokenResponse(response.result().card().primaryAccountNumber());

        String token = null;

        if (tokenResponse != null && tokenResponse.maskedPan() != null)
            token = tokenResponse.maskedPan();
        return token;
    }

    private static String validatePhone(HumoCardInfoResponse response) {
        String phone = null;

        if (response.result().mb() != null && response.result().mb().phone() != null) {
            phone = improvePhone(response.result().mb().phone());
        }
        return phone;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Card> getAll(Boolean isActive) {
        return repository.getCardsByStatus(isActive);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Card> findAllByPC(Processing processing) {
        log.info("Find all {} cards", processing);
        return repository.findAllByProcessing(processing);
    }

    @Override
    @Transactional
    public Card create(MasterBalanceResponse result, boolean isConfirmed, Processing pc) {
        var cardOptional = findCardByToken(result.getToken());

        if (cardOptional.isPresent()) {
            var card = cardOptional.get();

            card.setToken(result.getToken());
            card.setHolder(result.getHolder());
            card.setPhone(result.getPhone());

            return repository.save(card);
        }

        var currency = CardUtils.isSumPC(pc) ? Currency.UZS : Currency.USD;

        var preparedEntity = new Card(
                result.getPan(),
                "1350",
                result.getHolder(),
                pc,
                result.getPhone(),
                result.getToken(),
                isConfirmed,
                currency
        );

        log.info("Creating new master card entity: {}", preparedEntity);

        return repository.save(preparedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HumoLoanCardProjection> findHumoLoanCardsByLoanExtIds(List<String> loanExtIds) {
        return repository.findHumoLoanCardsByLoanExtIds(
                loanExtIds,
                Processing.HUMO,
                List.of(Payer.BORROWER, Payer.CO_BORROWER)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<HumoAutoDebitCardProjection> findActiveAutoDebitHumoCardsByKafkaPartitions(
            Collection<Integer> partitions,
            int partitionCount
    ) {
        if (partitions == null || partitions.isEmpty() || partitionCount <= 0) {
            return List.of();
        }

        return repository.findActiveAutoDebitHumoCardsByKafkaPartitions(
                Processing.HUMO,
                List.of(Payer.BORROWER, Payer.CO_BORROWER),
                partitions,
                partitionCount
        );
    }
}
