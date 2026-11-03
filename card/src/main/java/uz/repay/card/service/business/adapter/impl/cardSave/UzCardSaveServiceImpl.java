package uz.repay.card.service.business.adapter.impl.cardSave;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.CardSaverParamDTO;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;
import uz.repay.card.model.dto.uzcard.request.CheckCard2PinflRequest;
import uz.repay.card.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
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
public class UzCardSaveServiceImpl implements CardSaveService {
    private final UzCardService uzCardService;
    private final CardDAO cardDAO;
    private final LoanCardDAO loanCardDAO;

    @Override
    public void saveCardByToken(CardSaverParamDTO<?> cardSaverDTO) {
        var tokenList = cardSaverDTO.getCardList();
        log.info("Save uzCard cards by token with size: {}", tokenList.size());

        var afterHandledTokens = getOnlyUzCardsByToken(tokenList);

        if (afterHandledTokens.isEmpty()) {
            log.error("Exit method: Cause list is empty after checking");
            return;
        }

        //remove cards which opened to another Person Code
        saveCard(cardSaverDTO, afterHandledTokens);

        log.info("Finished save uzCard cards method by token");
    }

    private void saveCard(CardSaverParamDTO<?> cardSaverDTO, ArrayList<String> afterHandledTokens) {
        var loan = cardSaverDTO.getLoan();
        log.info("Begin {} cards to save loan: {}", afterHandledTokens.size(), loan.getExtId());

        if (!cardSaverDTO.getIsChecked())
            afterHandledTokens
                    .removeIf(t -> !checkCardBelongToPinfl(t, loan.getPinfl()));

        if (afterHandledTokens.isEmpty()) {
            log.info("Exit method: Cause list is empty after checking");
            return;
        }

        var newCardsResponse = getCardsInfo(afterHandledTokens);


        if (newCardsResponse.getError() != null) {
            log.error("Cards Info has error: {}", newCardsResponse.getError().getMessage());
            return;
        }


        if (newCardsResponse.getData().getError() != null) {
            log.error("Cards Info has error result: {}", newCardsResponse.getData().getError().getMessage());
            return;
        }

        List<CardBaseResult> cardResultList = newCardsResponse
                .getData()
                .getResult();

        removeAlreadyHasCards(loan, cardResultList);

        cardResultList
                .forEach(n -> saveCard(n, loan));

        log.info("End cards save method!");
    }

    private void saveCard(CardBaseResult n, Loan loan) {
        try {
            var preparedCard = new Card(
                    n.getPan(),
                    "1249",
                    n.getFullName(),
                    Processing.UZCARD,
                    n.getPhone(),
                    n.getId(),
                    true,
                    Currency.UZS
            );

            var card = cardDAO.create(preparedCard);
            loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO, true);

            String pan = n.getPan();

            if (!loan.getPans().contains(pan))
                loan.getPans().add(pan);
        } catch (Exception e) {
            log.error("Occurred error while saving card: {} with message: {}", n.getId(), e.getMessage());
        }
    }

    @Override
    public void saveCardByPan(CardSaverParamDTO<?> cardSaverDTO) {
        var panList = cardSaverDTO.getCardList();
        log.info("Save uzCard cards by pan with size: {}", panList.size());

        var afterHandledPans = getOnlyUzCardsByPan(panList);

        if (afterHandledPans.isEmpty()) {
            log.error("Exit method: Cause list is empty after checking");
            return;
        }

        var afterHandledTokens = new ArrayList<>(
                afterHandledPans
                        .stream()
                        .map(this::getP2PInfo)
                        .filter(SimpleResponse::getStatus)
                        .filter(p -> p.getData().getError() == null)
                        .map(s -> s.getData().getResult().getId())
                        .toList()
        );


        //remove cards which opened to another Person Code
        saveCard(cardSaverDTO, afterHandledTokens);

        log.info("Finished save uzCard cards method by token");
    }

    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.UZCARD);
    }

    private ArrayList<String> getOnlyUzCardsByToken(List<String> cards) {
        log.info("Taken {} cards", cards.size());

        var newList = new ArrayList<String>();

        cards
                .forEach(
                        c -> {
                            if (c.matches(CardUtils.regexUzCardToken()))
                                newList.add(c);
                        }

                );

        log.info("Only uzCards after removing: {}", newList);
        return newList;
    }

    private ArrayList<String> getOnlyUzCardsByPan(List<String> cards) {
        log.info("Taken {} cards", cards.size());

        var newList = new ArrayList<String>();

        cards
                .forEach(
                        c -> {
                            if (c.matches(CardUtils.regexUzCard()))
                                newList.add(c);
                        }

                );

        log.info("Only uzCards after removing: {}", newList);
        return newList;
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

    public SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> getCardsInfo(ArrayList<String> list) {
        var cardsInfoRequest = new BalanceRequest(list);

        try {
            return new SimpleResponse<>(uzCardService.getCardsInfo(cardsInfoRequest));
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

    private static void removeAlreadyHasCards(Loan loan, List<CardBaseResult> cards) {
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
                                .anyMatch(s -> s.equals(u.getId()))
                );

        log.info("New cards after removing: {}", cards);
    }
}
