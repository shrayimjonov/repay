package uz.repay.card.service.business.impl;

import feign.RetryableException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.backlash.BacklashEventDTO;
import uz.repay.card.model.dto.backlash.CardDeactivationDTO;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.BalanceResult;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.service.business.AutoPayInfoAdapterService;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoPayInfoAdapterServiceImpl implements AutoPayInfoAdapterService {
    private final UzCardService uzCardService;
    private final ApplicationEventPublisher eventPublisher;
    private final CardDAO cardDAO;

    @Override
    public List<LoanCardsResult> getCardsBalance(Map<Long, String> cardList) {
        var responseList = new ArrayList<LoanCardsResult>();
        log.info("Fetch {} autoPay cards balance", cardList.size());

        var tokens = new ArrayList<>(cardList.values().stream().toList());

        if (tokens.isEmpty())
            return responseList;

        var startTime = System.currentTimeMillis();

        var result = getBalanceInfo(tokens);


        if (result.getError() != null) {
            log.error("Card info has error: {}", result.getError().getMessage());

            if (result.getError().getCode().equals(510)) {
                log.warn("Try to disable: {} processing after un success fetching cards info", Processing.UZCARD);

                eventPublisher.publishEvent(
                        new BacklashEventDTO(
                                System.currentTimeMillis() - startTime,
                                Processing.SVGATE_AUTOPAY
                        )
                );
            }
            return responseList;
        }


        if (result.getData().getError() != null) {
            log.error("Card info has error: {}", result.getData().getError().getMessage());

            ifTokensNotFoundSetNotActive(cardList, result.getData().getError());
            return responseList;
        }


        var mutableCards = notFoundCardIds(cardList, result.getData().getResult().getBalanceInfo());

        if (!mutableCards.isEmpty())
            mutableCards
                    .forEach(
                            m -> eventPublisher.publishEvent(new CardDeactivationDTO(m))
                    );


        cardList
                .forEach(
                        (k, v) -> result.getData().getResult()
                                .getBalanceInfo()
                                .forEach(
                                        u -> {
                                            if (u.getId().equals(v)) {
                                                var card = cardDAO.findById(k);
                                                var preparedDTO = new LoanCardsResult(k, u, card, Processing.SVGATE_AUTOPAY);
                                                responseList.add(preparedDTO);
                                            }
                                        }
                                )
                );

        log.info("AutoPay cards fetch balance finished");
        return responseList;
    }

    private SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> getCardsInfo(ArrayList<String> tokenList) {
        try {
            var result = uzCardService.getCardsInfo(new BalanceRequest(tokenList));
            return new SimpleResponse<>(result);
        } catch (RetryableException r) {
            return new SimpleResponse<>(510, r.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    private SimpleResponse<UzCardBaseResponse<BalanceResult>> getBalanceInfo(ArrayList<String> tokenList) {
        try {
            var result = uzCardService.getCardsBalance(new BalanceRequest(tokenList));
            return new SimpleResponse<>(result);
        } catch (RetryableException r) {
            return new SimpleResponse<>(510, r.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    private List<Long> notFoundCardIds(Map<Long, String> cards, ArrayList<BalanceResult.BalanceInfo> results) {
        var notFoundList = new ArrayList<Long>();

        cards
                .forEach((k, v) -> {
                    if (results
                            .stream()
                            .noneMatch(
                                    r -> v.equals(r.getId())
                            ))
                        notFoundList.add(k);

                });
        log.warn("Not found cards from response card balance: {}", notFoundList);
        return notFoundList;
    }

    private void ifTokensNotFoundSetNotActive(Map<Long, String> cardList, CoreError error) {
        log.warn("Not found cards from response card balance: {}", cardList.values());

        if (error.getCode().equals(-257)) {
            cardList
                    .forEach(
                            (k, v) -> eventPublisher.publishEvent(new CardDeactivationDTO(k))
                    );

        }
    }

    @Override
    public LoanCardsResult getOneCardBalance(String card) {
        log.error("Method getOneCardBalance not necessary for processing: {}", Processing.SVGATE_AUTOPAY);
        return null;
    }

    @Override
    public CardShortInfoDTO takeP2PInfo(@NonNull String pan) {
        log.error("Method takeP2PInfo not necessary for processing: {}", Processing.SVGATE_AUTOPAY);
        return null;
    }
}
