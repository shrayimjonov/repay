package uz.repay.card.service.business.component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.entity.Card;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class AutoPayToSVGateConverter extends QuartzJobBean {
    private final UzCardService uzCardService;
    private final CardDAO cardDAO;

    @Async
    //   @Scheduled(cron = "0 0 6 * * ?")
    @SchedulerLock(name = "checkAutoPayCardsToSVGate", lockAtMostFor = "PT1H", lockAtLeastFor = "PT30M")
    public void checkAutoPayCardsToSVGate() {
        checkCardsToken();
    }

    private void checkCardsToken() {
        log.info("Starting check autoPay cards to SVGate converter...");

        var autoPayCards = cardDAO.findAllByPC(Processing.SVGATE_AUTOPAY);

        if (autoPayCards.isEmpty()) {
            log.warn("AutoPay cards is empty and will be exited!");
            return;
        }

        var separatedList = separateMap(
                autoPayCards.stream().map(Card::getToken).toList()
        );

        var svGateCards = new ArrayList<>();

        for (var separatedCards : separatedList) {
            log.info("Check cards: {}", separatedCards);
            var result = cardsInfoResult(separatedCards);

            if (!result.getStatus()) {
                log.info("Continue to next list, cause taken error");
                continue;
            }

            if (result.getData().getError() != null) {
                log.info("Continue to next list, cause response not success");
                continue;
            }

            result
                    .getData()
                    .getResult()
                    .forEach(this::changeCardProcessing);

            svGateCards.addAll(result.getData().getResult().stream().map(CardBaseResult::getId).toList());
        }

        log.info("Finished check autoPay cards with size: {}", svGateCards.size());
    }

    private void changeCardProcessing(CardBaseResult c) {
        log.info("Change card: {} {} from autoPay to svGate", c.getId(), c.getPan());

        try {
            var cardOptional = cardDAO.findCardByToken(c.getId());


            if (cardOptional.isPresent()) {
                var card = cardOptional.get();

                log.info("autoPay card entity before handling: {}", card);
                card.setPhone(c.getPhone());
                card.setProcessing(Processing.UZCARD);
                cardDAO.update(card);

                log.info("autoPay card converted to svGate card entity: {}", card);
            } else {
                log.error("Card: {} {} not found in database for converting from autoPay to svGate", c.getId(), c.getPan());
            }
        } catch (Exception e) {
            log.error("Error occurred while changing card: {} ", e.getMessage());
        }
    }

    private SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> cardsInfoResult(List<String> s) {
        var balanceRequest = new BalanceRequest(new ArrayList<>(s));
        try {
            var balanceResponse = uzCardService.getCardsInfo(balanceRequest);
            log.info("autoPay convert method response: {}", balanceResponse);
            return new SimpleResponse<>(balanceResponse);
        } catch (Exception e) {
            log.error("autoPay convert method takes exception: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    public static List<List<String>> separateMap(List<String> list) {

        List<List<String>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += 5) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + 5)))
            );
        }
        return parts;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        log.info("Started quartz job...");
        checkCardsToken();
    }
}
