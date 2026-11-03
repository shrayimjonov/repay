package uz.repay.card.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.backlash.BacklashEventDTO;
import uz.repay.card.model.dto.backlash.CardDeactivationDTO;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.visa.request.VisaCardsInfoRequest;
import uz.repay.card.service.business.VisaDavrInfoAdapterService;
import uz.repay.card.service.web.VisaMiddlewareService;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisaDavrInfoAdapterServiceImpl implements VisaDavrInfoAdapterService {
    private final VisaMiddlewareService visaMiddlewareService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<LoanCardsResult> getCardsBalance(Map<Long, String> cardList) {
        var responseList = new ArrayList<LoanCardsResult>();
        log.info("Fetch {} DAVR_VISA cards balance", cardList.size());

        var startTime = System.currentTimeMillis();

        if (cardList.isEmpty())
            return responseList;

        var pans = new ArrayList<>(cardList.values().stream().toList());

        var response = getCardsBalance(pans);


        if (response.getError() != null) {
            log.error("Card info has error: {}", response.getError().getMessage());

            if (response.getError().getCode().equals(510)) {
                log.warn("Try to disable: {} processing after un success fetching cards info", Processing.DAVR_VISA);

                eventPublisher.publishEvent(
                        new BacklashEventDTO(
                                System.currentTimeMillis() - startTime,
                                Processing.UZCARD
                        )
                );
            }
            return responseList;
        }


        var mutableCards = notFoundCardIds(cardList, response.getData().getResult());

        setNotActiveCardAndSave(mutableCards);

        cardList
                .forEach(
                        (k, v) -> response.getData().getResult()
                                .forEach(
                                        u -> {
                                            if (u.getId().equals(v)) {
                                                var preparedDTO = new LoanCardsResult(k, u, Processing.DAVR_VISA);
                                                responseList.add(preparedDTO);
                                            }
                                        }
                                )
                );


        log.info("DAVR_VISA cards fetch balance finished");
        return responseList;
    }

    private void setNotActiveCardAndSave(List<Long> mutableCards) {
        if (!mutableCards.isEmpty())
            mutableCards
                    .forEach(
                            m -> eventPublisher.publishEvent(new CardDeactivationDTO(m))
                    );
    }

    private SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> getCardsBalance(ArrayList<String> pans) {
        var visaBalanceRequest = new VisaCardsInfoRequest(
                "repay_card_" + System.currentTimeMillis(),
                pans
        );

        return visaMiddlewareService.getCardsInfo(visaBalanceRequest);
    }


    private List<Long> notFoundCardIds(Map<Long, String> cards, List<CardBaseResult> results) {
        var notFoundList = new ArrayList<Long>();

        cards
                .forEach((k, v) -> {
                    if (results
                            .stream()
                            .anyMatch(
                                    r -> r.getId().equals(v) && r.getPan().equals("CARD NOT FOUND")
                            ))
                        notFoundList.add(k);

                });
        log.warn("Not found cards from response DAVR_VISA card balance: {}", notFoundList);
        return notFoundList;
    }


    @Override
    public CardShortInfoDTO takeP2PInfo(@NonNull String pan) {
        log.info("Take p2p info for visa: {}", pan);

        var request = new VisaCardsInfoRequest(
                "REPAY_" + System.currentTimeMillis(),
                new ArrayList<>(List.of(pan))
        );

        var response = visaMiddlewareService.getCardsInfo(request);

        if (!response.getStatus())
            throw new GeneralErrorException(response.getError().getMessage());

        CardBaseResult cardInfo = response.getData().getResult().getFirst();

        if (cardInfo.getPan().equals("CARD NOT FOUND"))
            throw new GeneralErrorException("Card: %s not found".formatted(pan));


        return new CardShortInfoDTO(cardInfo);
    }

    @Override
    public LoanCardsResult getOneCardBalance(String card) {

        var response = getCardsBalance(new ArrayList<>(List.of(card)));

        if (!response.getStatus())
            throw new GeneralErrorException(response.getError().getMessage());

        if (response.getData().getError() != null)
            throw new GeneralErrorException(response.getData().getError().getMessage());

        var result = response.getData().getResult().getFirst();

        if (result.getPan().equals("CARD NOT FOUND"))
            throw new GeneralErrorException("Card: %s not found".formatted(card));

        return new LoanCardsResult(
                null,
                result,
                Processing.DAVR_VISA
        );
    }
}
