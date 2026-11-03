package uz.repay.card.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.core.request.card.CardInfoParam;
import uz.repay.card.model.dto.core.request.card.CardSaverParamDTO;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardSaveService;
import uz.repay.card.service.business.bean.CardBeanRegistrar;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class CardFinderComponent {
    private final CardBeanRegistrar cardBeanRegistrar;
    private final RedissonClient redissonClient;
    private final static Integer LOCK_WAIT_TIME = 1;
    private final static Integer LOCK_TTL = 300;


    public void findCardsFromFreeDetectors(Loan loan) {
        String loanExtId = loan.getExtId();
        log.info("Begin find card from free detectors by loanExtId: {}", loanExtId);

        RLock lock = redissonClient.getLock("card-finder-lock for loan:" + loanExtId);

        boolean isLocked = false;
        try {
            // Пытаемся захватить блокировку на 5 секунд, с автоматическим освобождением через 30 сек
            isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_TTL, TimeUnit.SECONDS);

            if (!isLocked)
                throw new InvalidDataException("Анкета: %s уже используется в другой транзакции".formatted(loanExtId));

            detectCardsAndSave(loan, false);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GeneralErrorException("Ошибка блокировки: %s".formatted(e.getMessage()));
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock(); // Освобождаем лок
            }
        }

    }

    public void findCardsFromAllDetectors(Loan loan) {
        String loanExtId = loan.getExtId();
        log.info("Begin find card from all detectors by loanExtId: {}", loanExtId);

        RLock lock = redissonClient.getLock("card-finder-lock for loan:" + loanExtId);

        boolean isLocked = false;
        try {
            // Пытаемся захватить блокировку на 5 секунд, с автоматическим освобождением через 30 сек
            isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_TTL, TimeUnit.SECONDS);

            if (!isLocked)
                throw new InvalidDataException("Анкета: %s уже используется в другой транзакции".formatted(loanExtId));

            detectCardsAndSave(loan, true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GeneralErrorException("Ошибка блокировки: %s".formatted(e.getMessage()));
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void detectCardsAndSave(Loan loan, boolean usePaidMethods) {

        var cardDetectorServiceList = cardBeanRegistrar.cardDetectorServices();

        log.info("Using paid methods: {}", usePaidMethods);

        var foundCardsList = cardDetectorServiceList
                .stream()
                .filter(z -> {
                    if (!usePaidMethods) {
                        return !z.isPaid();
                    }
                    return true;
                })
                .map(z -> z.getCardsByPersonCode(loan))
                .filter(SimpleResponse::getStatus)
                .map(SimpleResponse::getData)
                .toList();

        log.info("Finished card detecting...");

        var groupedByProcessing =
                foundCardsList.stream()
                        .flatMap(f -> f.getParam()
                                .stream()
                                .map(p -> Map.entry(p.getProcessing(), f)))
                        .collect(
                                Collectors.groupingBy(
                                        Map.Entry::getKey,
                                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                                )
                        );

        groupedByProcessing.forEach(
                (processing, groupedResultDTOs) -> {
                    var cardSaveServiceResult = cardBeanRegistrar.getCardSaveServiceByPC(processing);

                    if (!cardSaveServiceResult.getStatus()) {
                        log.error("Card save result has error for processing {}: {}", processing, cardSaveServiceResult.getError().getMessage());
                        return;
                    }

                    var service = cardSaveServiceResult.getData();

                    groupedResultDTOs
                            .forEach(f -> f.getParam().stream()
                                    .distinct()
                                    .filter(p -> p.getProcessing() == processing)
                                    .flatMap(p -> p.getParams().stream())
                                    .forEach(z -> saveCardsByType(f, z, service))
                    );
                });


        log.info("End find card method is over naturally");
    }

    private static void saveCardsByType(CardSaverResultDTO<?> f, CardInfoParam z, CardSaveService service) {
        final CardSaverParamDTO<?> cardSaverDTO = new CardSaverParamDTO<>(
                f.getLoan(),
                z.getCards(),
                f.getIsChecked(),
                f.getData()
        );

        switch (z.getCardType()) {
            case CardType.pan -> service.saveCardByPan(cardSaverDTO);
            case CardType.token -> service.saveCardByToken(cardSaverDTO);
            default -> log.error("Card type: {} not configured for saving!", z.getCardType());
        }
    }


    @Deprecated
    private void cardsHandler(List<CardSaverResultDTO<?>> foundCardsList) {
        foundCardsList
                .forEach(f -> f.getParam()
                        .forEach(s -> {
                                    var cardSaveServiceResult = cardBeanRegistrar.getCardSaveServiceByPC(s.getProcessing());

                                    if (!cardSaveServiceResult.getStatus()) {
                                        log.error("Card save result has error: {}", cardSaveServiceResult.getError().getMessage());
                                        return;
                                    }

                                    var service = cardSaveServiceResult.getData();

                                    s.getParams()
                                            .forEach(z -> saveCardsByType(f, z, service));

                                }
                        )
                );

    }

}
