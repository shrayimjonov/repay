package uz.repay.card.service.business.adapter.impl.cardSave;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.CardSaverParamDTO;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.visa.request.VisaCardsInfoRequest;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardSaveService;
import uz.repay.card.service.business.adapter.impl.infoAdapterV2.VisaInfoAdapterV2ServiceImpl;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.web.VisaMiddlewareService;
import uz.repay.card.util.SystemTool;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class VisaCardSaveServiceImpl implements CardSaveService {
    private final VisaMiddlewareService visaMiddlewareService;
    private final CardDAO cardDAO;
    private final LoanCardDAO loanCardDAO;

    @Override
    public void saveCardByToken(CardSaverParamDTO<?> cardSaverDTO) {
        var tokenList = cardSaverDTO.getCardList();
        log.info("Save VISA cards by token with size: {}", tokenList.size());

        var afterHandledTokens = getOnlyCardsByToken(tokenList);

        if (afterHandledTokens.isEmpty()) {
            log.error("Exit method: Cause list is empty after checking");
            return;
        }

        var afterHandledPans = new ArrayList<String>();

        afterHandledTokens
                .forEach(a -> {

                            var panResult = visaMiddlewareService.getRealPan(a);

                            if (!panResult.getStatus()) {
                                log.error("Exit method: Cause occurred error: {}", panResult.getError());
                                return;
                            }

                            if (panResult.getData().getError() != null) {
                                log.error("Exit method: Cause VISA error: {}", panResult.getData().getError());
                                return;
                            }

                            afterHandledPans.add(panResult.getData().getResult().getPan());
                        }
                );


        saveCard(cardSaverDTO, afterHandledPans);

        log.info("Finished save VISA cards method by token");
    }

    @Override
    public void saveCardByPan(CardSaverParamDTO<?> cardSaverDTO) {
        var panList = cardSaverDTO.getCardList();
        log.info("Save VISA cards by pan with size: {}", panList.size());

        var afterHandledPans = getOnlyCardsByPan(panList);

        if (afterHandledPans.isEmpty()) {
            log.error("Exit method: Cause list is empty after checking");
            return;
        }

        saveCard(cardSaverDTO, afterHandledPans);

        log.info("Finished save VISA cards method by pan");
    }

    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.DAVR_VISA);
    }

    private ArrayList<String> getOnlyCardsByToken(List<String> cards) {
        log.info("Taken {} cards", cards.size());

        var newList = new ArrayList<String>();

        cards
                .forEach(
                        c -> {
                            if (c.matches(CardUtils.regexVisaDAVRToken()))
                                newList.add(c);
                        }

                );

        log.info("Only VISA after removing: {}", newList);
        return newList;
    }


    private ArrayList<String> getOnlyCardsByPan(List<String> cards) {
        log.info("Taken {} cards", cards.size());

        var newList = new ArrayList<String>();

        cards
                .forEach(
                        c -> {
                            if (c.matches(CardUtils.regexVisaDAVR()))
                                newList.add(c);
                        }

                );

        log.info("Only VISA after removing: {}", newList);
        return newList;
    }

    private void saveCard(CardSaverParamDTO<?> cardSaverDTO, ArrayList<String> afterHandledPans) {
        var loan = cardSaverDTO.getLoan();
        log.info("Begin {} cards to save loan: {}", afterHandledPans.size(), loan.getExtId());

        if (!cardSaverDTO.getIsChecked())
            log.warn("Davr VISA hasn't method for checking cardholder and client to person code");

        var newCardsResponse = getCardsInfo(afterHandledPans);


        if (newCardsResponse.getError() != null) {
            log.error("Cards Info has error: {}", newCardsResponse.getError().getMessage());
            return;
        }


        if (newCardsResponse.getData().getError() != null) {
            log.error("Cards Info has error result: {}", newCardsResponse.getData().getError().getMessage());
            return;
        }

        var accessedTokens = new ArrayList<>(
                newCardsResponse
                        .getData()
                        .getResult()
                        .stream()
                        .filter(z -> !z.getPan().equals(VisaInfoAdapterV2ServiceImpl.CARD_NOT_FOUND_MESSAGE))
                        .toList()
        );


        removeAlreadyHasCards(loan, accessedTokens);

        accessedTokens

                .forEach(n -> saveCard(n, loan));

        log.info("End cards save method!");
    }

    private void saveCard(CardBaseResult n, Loan loan) {
        try {
            var preparedCard = new Card(
                    n.getPan(),
                    n.getExpiry(),
                    n.getFullName(),
                    Processing.DAVR_VISA,
                    n.getPhone(),
                    n.getId(),
                    true,
                    Currency.UZS
            );

            var card = cardDAO.create(preparedCard);
            loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO, true);


            var maskedPan = CardUtils.maskPanDigits(n.getPan());

            if (!loan.getPans().contains(maskedPan))
                loan.getPans().add(maskedPan);
        } catch (Exception e) {
            log.error("Occurred error while saving card: {} with message: {}", n.getId(), e.getMessage());
        }
    }


    private static void removeAlreadyHasCards(Loan loan, ArrayList<CardBaseResult> panList) {
        var loanCardsList = new ArrayList<>(loan
                .getLoanCards()
                .stream()
                .filter(z -> z.getCard().getProcessing().equals(Processing.DAVR_VISA))
                .map(lc -> CardUtils.decryptBulkPan(lc.getCard().getPan()))
                .toList());

        log.info("Loan: {} has {} cards", loan.getExtId(), loanCardsList.size());


        panList
                .removeIf(
                        hc -> loanCardsList
                                .stream()
                                .anyMatch(s -> s.equals(hc.getPan()))
                );

        log.info("New cards after removing: {}", panList);
    }

    public SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> getCardsInfo(ArrayList<String> list) {
        var cardsInfoRequest = new VisaCardsInfoRequest(
                SystemTool.requestIdGenerator(),
                list
        );
        return visaMiddlewareService.getCardsInfo(cardsInfoRequest);
    }
}
