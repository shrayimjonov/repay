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
import uz.repay.card.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.service.business.UzCardInfoAdapterService;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.exception.CardException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UzCardInfoAdapterServiceImpl implements UzCardInfoAdapterService {
    private final UzCardService uzCardService;
    private final ApplicationEventPublisher eventPublisher;
    private final static String UZ_CARD_TYPE_CORPORATE = "corporate";

    @Override
    public List<LoanCardsResult> getCardsBalance(Map<Long, String> cardList) {
        var responseList = new ArrayList<LoanCardsResult>();
        log.info("Fetch {} uzCards balance", cardList.size());

        var tokens = new ArrayList<>(cardList.values().stream().toList());

        if (tokens.isEmpty())
            return responseList;

        var startTime = System.currentTimeMillis();

        var result = getCardsInfo(tokens);


        if (result.getError() != null) {
            log.error("Card info has error: {}", result.getError().getMessage());

            if (result.getError().getCode().equals(510)) {
                log.warn("Try to disable: {} processing after un success fetching cards info", Processing.UZCARD);

                eventPublisher.publishEvent(
                        new BacklashEventDTO(
                                System.currentTimeMillis() - startTime,
                                Processing.UZCARD
                        )
                );
            }
            return responseList;
        }


        if (result.getData().getError() != null) {
            log.error("Card info has error: {}", result.getData().getError().getMessage());

            ifTokensNotFoundSetNotActive(cardList, result);
            return responseList;
        }


        var mutableCards = notFoundCardIds(cardList, result.getData().getResult());

        if (!mutableCards.isEmpty())
            mutableCards
                    .forEach(
                            m -> eventPublisher.publishEvent(new CardDeactivationDTO(m))
                    );


        cardList
                .forEach(
                        (k, v) -> result.getData().getResult()
                                .forEach(
                                        u -> {
                                            if (u.getId().equals(v)) {
                                                var preparedDTO = new LoanCardsResult(k, u, Processing.UZCARD);
                                                responseList.add(preparedDTO);
                                            }
                                        }
                                )
                );

        log.info("UzCards fetch balance finished");
        return responseList;
    }


    private void ifTokensNotFoundSetNotActive(Map<Long, String> cardList, SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> result) {
        log.warn("Not found cards from response card balance: {}", cardList.values());

        if (result.getData().getError().getCode().equals(-257)) {
            cardList
                    .forEach(
                            (k, v) -> eventPublisher.publishEvent(new CardDeactivationDTO(k))
                    );

        }
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

    private List<Long> notFoundCardIds(Map<Long, String> cards, List<CardBaseResult> results) {
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

    @Override
    public CardShortInfoDTO takeP2PInfo(@NonNull String pan) {
        log.info("Take UzCard p2p info for: {}", pan);

        var request = new P2PInfoRequest(pan);

        var response = uzCardService.getP2PInfo(request);

        if (response.getError() != null)
            throw new GeneralErrorException(response.getError().getMessage());


        if (response.getResult().getType().equals(UZ_CARD_TYPE_CORPORATE))
            throw new CardException("Card: %s type is: %s".formatted(pan, response.getResult().getType()));

        return new CardShortInfoDTO(
                response.getResult()
        );
    }

    @Override
    public LoanCardsResult getOneCardBalance(String card) {
        var response = getCardsInfo(new ArrayList<>(List.of(card)));

        if (!response.getStatus())
            throw new GeneralErrorException(response.getError().getMessage());

        if (response.getData().getError() != null)
            throw new GeneralErrorException(response.getData().getError().getMessage());


        return new LoanCardsResult(
                null,
                response.getData().getResult().getFirst(),
                Processing.UZCARD
        );
    }

}
