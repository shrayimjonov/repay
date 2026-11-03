package uz.repay.card.service.business.adapter.impl.cardSave;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.CardSaverParamDTO;
import uz.repay.card.model.dto.master.request.MasterBalanceRequest;
import uz.repay.card.model.dto.master.request.MasterP2PInfoRequest;
import uz.repay.card.model.dto.master.response.MasterBalanceResponse;
import uz.repay.card.model.dto.master.response.MasterP2PInfoResponse;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardSaveService;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.web.MasterClientService;
import uz.repay.common.model.dto.response.core.ResultData;
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
public class MasterCardSaveServiceImpl implements CardSaveService {
    private final MasterClientService masterClientService;
    private final CardDAO cardDAO;
    private final LoanCardDAO loanCardDAO;

    @Override
    public void saveCardByToken(CardSaverParamDTO<?> cardSaverDTO) {
        var tokenList = cardSaverDTO.getCardList();

        log.info("Save Davr Master cards by token with size: {}", tokenList.size());

        var afterHandledTokens = getOnlyCardsByToken(tokenList);

        if (afterHandledTokens.isEmpty()) {
            log.error("Exit method: Cause list is empty after checking");
            return;
        }

        saveCard(cardSaverDTO, afterHandledTokens);

        log.info("Finished save master cards method by token");
    }

    @Override
    public void saveCardByPan(CardSaverParamDTO<?> cardSaverDTO) {
        var panList = cardSaverDTO.getCardList();

        log.info("Save Davr Master cards by pan with size: {}", panList.size());

        var afterHandledPans = getOnlyCardsByPan(panList);

        if (afterHandledPans.isEmpty()) {
            log.error("Exit method: Cause list is empty after checking");
            return;
        }

        var afterHandledTokens = new ArrayList<>(
                afterHandledPans
                        .stream()
                        .map(this::getP2PInfoResult)
                        .filter(SimpleResponse::getStatus)
                        .filter(p -> p.getData().getError() == null)
                        .map(s -> s.getData().getResult().getToken())
                        .toList()
        );


        //remove cards which opened to another Person Code
        saveCard(cardSaverDTO, afterHandledTokens);

        log.info("Finished save master cards method by token");
    }


    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.DAVR_MASTER_CARD);
    }

    private ArrayList<String> getOnlyCardsByToken(List<String> cards) {
        log.info("Taken {} master cards", cards.size());

        var newList = new ArrayList<String>();

        cards
                .forEach(
                        c -> {
                            if (c.matches(CardUtils.regexDavrMasterCardToken()))
                                newList.add(c);
                        }

                );

        log.info("Only davr master after removing: {}", newList);
        return newList;
    }


    private void saveCard(CardSaverParamDTO<?> cardSaverDTO, ArrayList<String> afterHandledTokens) {
        var loan = cardSaverDTO.getLoan();
        log.info("Begin {} cards to save loan: {}", afterHandledTokens.size(), loan.getExtId());

        removeAlreadyHasCards(loan, afterHandledTokens);

        var accessedCards = afterHandledTokens
                .stream()
                .map(m -> {
                    var balanceRequest = new MasterBalanceRequest(m);
                    return masterClientService.balance(balanceRequest);
                })
                .filter(cardResult -> {

                    if (!cardResult.getStatus()) {
                        log.error("Exit method: Cause error: {}", cardResult.getError());
                        return false;
                    }

                    if (cardResult.getData().getError() != null) {
                        log.error("Exit method: BPC master returns error: {}", cardResult.getData().getError());
                        return false;
                    }

                    return true;
                })
                .map(z -> z.getData().getResult())
                .toList();

        if (!cardSaverDTO.getIsChecked())
            log.warn("Davr Master hasn't method for checking cardholder and client to person code");


        accessedCards
                .forEach(c -> saveCard(c, loan));


        log.info("End cards save method!");
    }

    private void saveCard(MasterBalanceResponse c, Loan loan) {
      try {
          String pan = c.getPan();


        var preparedCard = new Card(
                pan,
                "1249",
                c.getHolder(),
                Processing.DAVR_MASTER_CARD,
                c.getPhone(),
                c.getToken(),
                true,
                Currency.UZS
        );
        var card = cardDAO.create(preparedCard);
        loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO, true);


        if (!loan.getPans().contains(pan))
            loan.getPans().add(pan);

      } catch (Exception e) {
          log.error("Occurred error while saving card: {} with message: {}", c.getToken(), e.getMessage());
      }
    }


    private static void removeAlreadyHasCards(Loan loan, ArrayList<String> tokens) {
        if (tokens.isEmpty()) {
            log.warn("Tokens list is empty and will be exited!");
            return;
        }

        var loanCardsList = new ArrayList<>(loan
                .getLoanCards()
                .stream()
                .filter(z -> z.getCard().getProcessing().equals(Processing.DAVR_MASTER_CARD))
                .map(z -> z.getCard().getToken())
                .toList());


        log.info("Loan: {} has {} cards", loan.getExtId(), loanCardsList.size());


        tokens
                .removeIf(
                        u -> loanCardsList
                                .stream()
                                .anyMatch(u::equals)
                );

        log.info("New tokens after removing: {}", tokens);
    }

    private ArrayList<String> getOnlyCardsByPan(List<String> cards) {
        log.info("Taken {} cards", cards.size());

        var newList = new ArrayList<String>();

        cards
                .forEach(
                        c -> {
                            if (c.matches(CardUtils.regexMasterCardDAVR()))
                                newList.add(c);
                        }

                );

        log.info("Only master cards after removing: {}", newList);
        return newList;
    }

    private SimpleResponse<ResultData<MasterP2PInfoResponse>> getP2PInfoResult(String pan) {
        var request = new MasterP2PInfoRequest(pan);
        return masterClientService.p2pInfo(request);
    }
}
