package uz.repay.card.service.business.adapter.impl.cardSave;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.CardSaverParamDTO;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoCheckCardToPINFLParam;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoRealPanRequest;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humo.response.HumoTokenResponse;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardSaveService;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.*;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;

import static uz.repay.card.util.ServiceUtils.improvePhone;


@Service
@Slf4j
@RequiredArgsConstructor
public class HuMoCardSaveServiceImpl implements CardSaveService {
    private final HuMoClientService huMoClientService;
    private final CardDAO cardDAO;
    private final LoanCardDAO loanCardDAO;
    private final static String CHECK_CARD_PINFL_SUCCESS_RESULT = "Yes";


    @Override
    public void saveCardByToken(CardSaverParamDTO<?> cardSaverDTO) {
        var tokenList = cardSaverDTO.getCardList();

        log.info("Save huMo cards by token with size: {}", tokenList.size());

        var afterHandledTokens = getOnlyCardsByToken(tokenList);

        if (afterHandledTokens.isEmpty()) {
            log.error("Exit method: Cause list is empty after checking");
            return;
        }

        var afterHandledPans = new ArrayList<String>();

        afterHandledTokens
                .forEach(a -> {
                            var request = new HuMoRealPanRequest(a);
                            var panResult = huMoClientService.getRealPan(request);

                            if (!panResult.getStatus()) {
                                log.error("Exit method: Cause occurred error: {}", panResult.getError());
                                return;
                            }

                            if (panResult.getData().getError() != null) {
                                log.error("Exit method: Cause HuMo error: {}", panResult.getData().getError());
                                return;
                            }

                            afterHandledPans.add(panResult.getData().getResult().getPan());
                        }
                );


        //remove cards which opened to another Person Code
        saveCard(cardSaverDTO, afterHandledPans);

        log.info("Finished save huMo cards method by token");
    }

    @Override
    public void saveCardByPan(CardSaverParamDTO<?> cardSaverDTO) {
        var panList = cardSaverDTO.getCardList();
        log.info("Save huMo cards by pan with size: {}", panList.size());

        var afterHandledPans = getOnlyCardsByPan(panList);

        if (afterHandledPans.isEmpty()) {
            log.error("Exit method: Cause list is empty after checking");
            return;
        }

        //remove cards which opened to another Person Code
        saveCard(cardSaverDTO, afterHandledPans);

        log.info("Finished save huMo cards method by pan");
    }

    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.HUMO);
    }

    private ArrayList<String> getOnlyCardsByToken(List<String> cards) {
        log.info("Taken {} cards", cards.size());

        var newList = new ArrayList<String>();

        cards
                .forEach(
                        c -> {
                            if (c.matches(CardUtils.regexHuMoToken()))
                                newList.add(c);
                        }

                );

        log.info("Only huMo after removing: {}", newList);
        return newList;
    }

    private ArrayList<String> getOnlyCardsByPan(List<String> cards) {
        log.info("Taken {} cards", cards.size());

        var newList = new ArrayList<String>();

        cards
                .forEach(
                        c -> {
                            if (c.matches(CardUtils.regexHumo()))
                                newList.add(c);
                        }

                );

        log.info("Only huMo after removing: {}", newList);
        return newList;
    }

    private void saveCard(CardSaverParamDTO<?> cardSaverDTO, ArrayList<String> afterHandledPans) {
        var loan = cardSaverDTO.getLoan();
        log.info("Begin {} cards to save loan: {}", afterHandledPans.size(), loan.getExtId());

        removeAlreadyHasCards(loan, afterHandledPans);

        var accessedCards = new ArrayList<>(afterHandledPans
                .stream()
                .map(this::getCardInfo)
                .filter(cardResult -> {
                    if (!cardResult.getStatus()) {
                        log.error("Exit method: Cause error: {}", cardResult.getError());
                        return false;
                    }

                    if (cardResult.getData().error() != null) {
                        log.error("Exit method: HuMo returns error: {}", cardResult.getData().error());
                        return false;
                    }
                    return true;
                })
                .map(SimpleResponse::getData)
                .toList());


        if (!cardSaverDTO.getIsChecked())
            accessedCards
                    .removeIf(z -> {

                        var request = new HuMoCheckCardToPINFLParam(
                                loan.getPinfl(),
                                z.result().card().primaryAccountNumber()
                        );


                        var cardResult = huMoClientService.checkCardToPINFL(request);

                        if (!cardResult.getStatus()) {
                            log.error("Exit method: Cause error: {}", cardResult.getError());
                            return true;
                        }

                        if (cardResult.getData().getError() != null) {
                            log.error("Exit method: HuMo returns error: {}", cardResult.getData().getError());
                            return true;
                        }

                        return !cardResult.getData().getResult().equals(CHECK_CARD_PINFL_SUCCESS_RESULT);
                    });


        accessedCards
                .forEach(h -> saveCard(h, loan));


        log.info("End cards save method!");
    }

    private void saveCard(HumoCardInfoResponse h, Loan loan) {
        HumoCardInfoResponse.HumoCard result = h.result().card();
        try {
            var preparedCard = new Card(
                    result.primaryAccountNumber(),
                    result.expiry(),
                    result.nameOnCard(),
                    Processing.HUMO,
                    validatePhone(h),
                    getHumoCardToken(h),
                    true,
                    Currency.UZS
            );

            if (preparedCard.getToken() == null) {
                log.info("Token is null. Can't save card: {}", preparedCard);
                return;
            }

            var card = cardDAO.create(preparedCard);
            loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO, true);

            String pan = result.primaryAccountNumber();

            var maskedPan = CardUtils.maskPanDigits(pan);

            if (!loan.getPans().contains(maskedPan))
                loan.getPans().add(maskedPan);

        } catch (Exception e) {
            log.error("Occurred error while saving card: {} with message: {}", result.primaryAccountNumber(), e.getMessage());
        }
    }

    private static String validatePhone(HumoCardInfoResponse response) {
        String phone = null;

        if (response.result().mb() != null && response.result().mb().phone() != null) {
            phone = improvePhone(response.result().mb().phone());
        }
        return phone;
    }

    public SimpleResponse<HumoCardInfoResponse> getCardInfo(String pan) {
        var cardsInfoRequest = new HumoCardInfoRequest(pan);

        try {
            return new SimpleResponse<>(huMoClientService.getCardInfo(cardsInfoRequest));
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

    private static void removeAlreadyHasCards(Loan loan, List<String> panList) {
        var loanCardsList = new ArrayList<>(loan
                .getLoanCards()
                .stream()
                .filter(z -> z.getCard().getProcessing().equals(Processing.HUMO))
                .map(lc -> CardUtils.decryptBulkPan(lc.getCard().getPan()))
                .toList());

        log.info("Loan: {} has {} cards", loan.getExtId(), loanCardsList.size());


        panList
                .removeIf(
                        hc -> loanCardsList
                                .stream()
                                .anyMatch(hc::equals)
                );

        log.info("New cards after removing: {}", panList);
    }

    private String getHumoCardToken(HumoCardInfoResponse response) {
        var tokenResponse = getHumoTokenResponse(response.result().card().primaryAccountNumber());

        String token = null;

        if (tokenResponse != null && tokenResponse.maskedPan() != null)
            token = tokenResponse.maskedPan();
        return token;
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
}
