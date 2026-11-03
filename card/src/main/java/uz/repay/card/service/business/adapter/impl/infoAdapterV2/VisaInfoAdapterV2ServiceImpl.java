package uz.repay.card.service.business.adapter.impl.infoAdapterV2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.adapter.CardBalanceFetchRequest;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.visa.request.VisaCardsInfoRequest;
import uz.repay.card.service.business.adapter.VisaInfoAdapterV2Service;
import uz.repay.card.service.business.component.DisableCardsComponent;
import uz.repay.card.service.web.VisaMiddlewareService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisaInfoAdapterV2ServiceImpl implements VisaInfoAdapterV2Service {
    private final VisaMiddlewareService visaMiddlewareService;
    public static final String CARD_NOT_FOUND_MESSAGE = "CARD NOT FOUND";
    private final DisableCardsComponent disableCardsComponent;

    @Override
    public SimpleResponse<List<LoanCardsResult>> getCardsBalance(List<CardBalanceFetchRequest> cardsList) {
        var responseList = new ArrayList<LoanCardsResult>();
        log.info("Fetch {} DAVR_VISA cards balance", cardsList.size());


        if (cardsList.isEmpty()) {
            log.info("Request list size empty and will be exited from Cards Balance method!");
            return new SimpleResponse<>(
                    List.of()
            );
        }

        var pans = new ArrayList<>(cardsList
                .stream()
                .filter(z -> {
                            if (!z.getType().equals(CardType.pan)) {
                                log.error("VISA card type must be pan! Now: {}", z.getType());
                                return false;
                            }

                            if (!z.getProcessing().equals(Processing.DAVR_VISA)) {
                                log.error("Processing must be DAVR_VISA! Now: {}", z.getProcessing());
                                return false;
                            }

                            if (!z.getCard().matches(CardUtils.regexVisaDAVR())) {
                                log.error("DAVR_VISA: {} doesn't match to pan regex!", z.getCard());
                                return false;
                            }

                            return true;
                        }
                )
                .map(CardBalanceFetchRequest::getCard)
                .toList());

        var response = getCardsBalanceByPan(pans);


        if (response.getError() != null) {
            log.error("DAVR_VISA card get Balance fetching error: {}", response.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), response.getError().getMessage());
        }

        if (response.getData().getError() != null) {
            log.error("DAVR_VISA card get Balance result is FAILED");
            cardsList.forEach(c -> responseList.add(new LoanCardsResult(c.getBaseCard())));
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), response.getData().getError().getMessage());
        }


        findNotFoundCardsAndDeactive(cardsList, response.getData().getResult());


        cardsList
                .forEach(
                        c -> response.getData().getResult()
                                .forEach(
                                        u -> {
                                            if (u.getId().equals(c.getCard()) && !u.getPan().equals(CARD_NOT_FOUND_MESSAGE)) {
                                                var preparedDTO = new LoanCardsResult(c.getCardId(), u, Processing.DAVR_VISA);
                                                responseList.add(preparedDTO);
                                            }
                                        }
                                )
                );


        log.info("DAVR_VISA cards fetch balance finished");
        return new SimpleResponse<>(responseList);
    }

    private SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> getCardsBalanceByPan(ArrayList<String> pans) {
        var visaBalanceRequest = new VisaCardsInfoRequest(
                "repay_card_" + System.currentTimeMillis(),
                pans
        );

        return visaMiddlewareService.getCardsInfo(visaBalanceRequest);
    }


    private void findNotFoundCardsAndDeactive(List<CardBalanceFetchRequest> cards, List<CardBaseResult> results) {
        var notFoundList = new ArrayList<Long>();

        cards
                .forEach(c -> {
                    if (results
                            .stream()
                            .anyMatch(
                                    r -> r.getId().equals(c.getCard()) && r.getPan().equals(CARD_NOT_FOUND_MESSAGE)
                            ))
                        notFoundList.add(c.getCardId());
                });

        log.warn("Not found cards from response DAVR_VISA card balance: {}", notFoundList);

        if (!notFoundList.isEmpty())
            notFoundList
                    .forEach(disableCardsComponent::setNotActiveCardAndSave);
    }

    @Override
    public SimpleResponse<LoanCardsResult> getOneCardBalance(CardBalanceFetchRequest request) {
        var card = request.getCard();

        log.info("Get one DAVR_VISA card balance: {}", card);

        if (!request.getType().equals(CardType.pan)) {
            var message = "DAVR_VISA card type must be pan! Now: %s".formatted(request.getType());
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }


        if (!request.getProcessing().equals(Processing.DAVR_VISA)) {
            var message = "Processing must be DAVR_VISA! Now: %s".formatted(request.getProcessing());
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }


        if (!card.matches(CardUtils.regexVisaDAVR())) {
            var message = "DAVR_VISA card: %s doesn't match to pan regex!".formatted(card);
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var response = getCardsBalanceByPan(
                new ArrayList<>(List.of(card))
        );

        if (!response.getStatus()) {
            log.error("DAVR_VISA card: {} balance fetching error: {}", card, response.getError().getMessage());
            return new SimpleResponse<>(response.getError().getCode(), response.getError().getMessage());
        }

        if (response.getData().getError() != null) {
            log.error("DAVR_VISA card: {} get balance result is FAILED", card);
            return new SimpleResponse<>(response.getData().getError().getCode(), response.getData().getError().getMessage());
        }

        var result = response.getData().getResult().getFirst();

        if (result.getPan().equals(CARD_NOT_FOUND_MESSAGE)) {
            var message = "DAVR_VISA card: %s not found!".formatted(card);
            log.error(message);
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), message);
        }


        LoanCardsResult loanCardsResult = new LoanCardsResult(
                request.getCardId(),
                result,
                Processing.DAVR_VISA
        );

        log.info("Prepared loan card result: {}", loanCardsResult);

        return new SimpleResponse<>(loanCardsResult);
    }

    @Override
    public SimpleResponse<CardShortInfoDTO> takeP2PInfo(String pan) {
        log.info("Take DAVR_VISA p2p info for card: {}", pan);

        if (!pan.matches(CardUtils.regexVisaDAVR())) {
            var message = "DAVR_VISA card: %s doesn't match to pan regex!".formatted(pan);
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var request = new VisaCardsInfoRequest(
                "REPAY_" + System.currentTimeMillis(),
                new ArrayList<>(List.of(pan))
        );

        var response = visaMiddlewareService.getCardsInfo(request);

        if (!response.getStatus()) {
            log.error("DAVR_VISA card: {} p2pInfo fetching error: {}", pan, response.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), response.getError().getMessage());
        }

        if (response.getData().getError() != null) {
            log.error("DAVR_VISA card: {} p2pInfo result is FAILED", pan);
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), response.getData().getError().getMessage());
        }

        CardBaseResult cardInfo = response.getData().getResult().getFirst();

        if (cardInfo.getPan().equals(CARD_NOT_FOUND_MESSAGE)) {
            var message = "DAVR_VISA card: %s not found!".formatted(pan);
            log.error(message);
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), message);
        }

        CardShortInfoDTO cardShortInfoDTO = new CardShortInfoDTO(
                cardInfo
        );
        log.info("Prepared p2pInfo result: {}", cardShortInfoDTO);

        return new SimpleResponse<>(cardShortInfoDTO);
    }


    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.DAVR_VISA);
    }
}
