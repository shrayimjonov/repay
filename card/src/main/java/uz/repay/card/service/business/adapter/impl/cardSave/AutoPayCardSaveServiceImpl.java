package uz.repay.card.service.business.adapter.impl.cardSave;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.CardSaverParamDTO;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;
import uz.repay.card.model.dto.uzcard.request.CheckCard2PinflRequest;
import uz.repay.card.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.BalanceResult;
import uz.repay.card.model.dto.uzcard.response.result.P2PInfoResult;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardSaveService;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.*;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class AutoPayCardSaveServiceImpl implements CardSaveService {
    private final UzCardService uzCardService;
    private final CardDAO cardDAO;
    private final LoanCardDAO loanCardDAO;

    @Override
    public void saveCardByToken(CardSaverParamDTO<?> cardSaverDTO) {
        log.info("Save autoPay cards by token");

        var autoPayCardsList = new ArrayList<SVGateAutoPayCardsResult>();

        if (cardSaverDTO.getData() instanceof List<?> resultList) {
            resultList.forEach(z -> autoPayCardsList.add((SVGateAutoPayCardsResult) z));
        }

        if (autoPayCardsList.isEmpty()) {
            log.error("Exit method: Cause autoPay cards list is empty");
            return;
        }

        var afterHandledTokens = getOnlyCardsByToken(autoPayCardsList);

        if (afterHandledTokens.isEmpty()) {
            log.error("Exit method: Cause list is empty after checking");
            return;
        }

        //remove cards which opened to another Person Code
        saveCard(cardSaverDTO, afterHandledTokens);

        log.info("Finished save uzCard cards method by token");
    }

    private void saveCard(CardSaverParamDTO<?> cardSaverDTO, ArrayList<SVGateAutoPayCardsResult> cards) {
        var loan = cardSaverDTO.getLoan();
        log.info("Begin {} cards to save loan: {}", cards.size(), loan.getExtId());

        if (!cardSaverDTO.getIsChecked())
            cards
                    .removeIf(t -> !checkCardBelongToPinfl(t.getToken(), loan.getPinfl()));

        if (cards.isEmpty()) {
            log.info("Exit method: Cause list is empty after checking");
            return;
        }


        removeAlreadyHasCards(loan, cards);

        cards
                .forEach(c -> saveCard(c, loan));

        log.info("End cards save method!");
    }

    private void saveCard(SVGateAutoPayCardsResult c, Loan loan) {
        try {
            var preparedCard = new Card(
                    c.getPan(),
                    "1249",
                    c.getFullName(),
                    Processing.SVGATE_AUTOPAY,
                    c.getPhone(),
                    c.getToken(),
                    true,
                    Currency.UZS
            );

            var card = cardDAO.create(preparedCard);
            loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO, true);

            String pan = c.getPan();
            if (!loan.getPans().contains(pan)) {
                loanCardDAO.create(loan, card, Payer.BORROWER, AddType.AUTO);
                loan.getPans().add(pan);
            }
        } catch (Exception e) {
            log.error("Occurred error while saving card: {} with message: {}", c.getToken(), e.getMessage());
        }
    }

    @Override
    public void saveCardByPan(CardSaverParamDTO<?> cardSaverDTO) {
        log.error("AutoPay doesn't work with PAN!");
    }

    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.SVGATE_AUTOPAY);
    }

    private ArrayList<SVGateAutoPayCardsResult> getOnlyCardsByToken(ArrayList<SVGateAutoPayCardsResult> cards) {
        log.info("Taken {} cards", cards.size());

        cards
                .removeIf(z -> !z.getToken().matches(CardUtils.regexUzCardToken()));

        log.info("Only autoPay after removing: {}", cards);
        return cards;
    }


    private Boolean checkCardBelongToPinfl(String token, String personCode) {
        log.info("Check {} card to PINFL: {} before saving", token, personCode);
        var response = uzCardService.checkCardIsBelongToPersonCode(
                new CheckCard2PinflRequest(token, personCode)
        );

        log.info("Card check to PINFL response: {}", response);

        if (response.getError() != null)
            return false;

        if (response.getResult() == null)
            return false;

        return response.getResult().getCode().equals(0);
    }

    public SimpleResponse<UzCardBaseResponse<BalanceResult>> getCardsBalance(ArrayList<String> list) {
        var cardsInfoRequest = new BalanceRequest(list);

        try {
            return new SimpleResponse<>(uzCardService.getCardsBalance(cardsInfoRequest));
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

    public SimpleResponse<UzCardBaseResponse<P2PInfoResult>> getP2PInfo(String pan) {
        var p2pInfoRequest = new P2PInfoRequest(pan);

        try {
            return new SimpleResponse<>(uzCardService.getP2PInfo(p2pInfoRequest));
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

    private static void removeAlreadyHasCards(Loan loan, ArrayList<SVGateAutoPayCardsResult> cards) {
        if (cards.isEmpty()) {
            log.warn("Cards list is empty and will be exited!");
            return;
        }

        var loanCardsList = new ArrayList<>(loan
                .getLoanCards()
                .stream()
                .map(z -> z.getCard().getToken())
                .toList());


        log.info("Loan: {} has {} cards", loan.getExtId(), loanCardsList.size());


        cards
                .removeIf(
                        u -> loanCardsList
                                .stream()
                                .anyMatch(s -> u.getToken().equals(s))
                );

        log.info("New cards after removing: {}", cards);
    }
}
