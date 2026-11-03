package uz.repay.card.service.core.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.master.request.MasterBalanceRequest;
import uz.repay.card.model.dto.master.request.MasterCustomerCardsByPINFLRequest;
import uz.repay.card.model.dto.master.response.MasterBalanceResponse;
import uz.repay.card.model.dto.master.response.MasterCustomerCardsResponse;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Guarantor;
import uz.repay.card.model.entity.GuarantorCard;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.model.entity.pk.GuarantorCardKey;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.GuarantorCardDAO;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.web.MasterClientService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.*;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class MasterCardAdderComponent {
    private final MasterClientService masterClientService;
    private final LoanCardDAO loanCardDAO;
    private final CardDAO cardDAO;
    private final GuarantorCardDAO guarantorCardDAO;

    public ResultData<CardFinderResponseDTO> getCardFinderResponse(Loan loan) {
        log.info("Starting master card find for loan: {}", loan.getExtId());

        var cards = new CardFinderResponseDTO();
        findCardsByPINFL(loan, cards);
        log.info("Finished master card finder for : {}", loan.getExtId());

        return ResultData.ok(cards);
    }

    private void findCardsByPINFL(Loan loan, CardFinderResponseDTO cards) {
        log.info("Begin find cards by pinfl: {}", loan.getPinfl());

        var validateResult = validateCardFinderResponse(loan.getPinfl());
        if (!validateResult.getStatus())
            return;

        var result = validateResult.getData();


        baseCardSaverMaster(loan, cards, result, Processing.DAVR_MASTER_CARD);
        log.info("Ended find MASTER cards...");
    }

    private void baseCardSaverMaster(Loan loan, CardFinderResponseDTO cards, List<MasterCustomerCardsResponse> tokenList, Processing processing) {
        if (!processing.equals(Processing.DAVR_MASTER_CARD)) {
            log.warn("Processing is not {} and will be returned!", Processing.DAVR_MASTER_CARD);
            return;
        }

        removeAlreadyHasCards(loan, tokenList);

        if (tokenList.isEmpty()) {
            log.warn("MASTER cards is empty and will be exited");
            return;
        }


        tokenList
                .forEach(c -> {
                            var cardResponse = fetchMasterCardBalance(c);

                            if (!cardResponse.getStatus())
                                return;

                            var cardResult = cardResponse.getData();

                            var preparedCard = new Card(
                                    c.getPan(),
                                    "1249",
                                    cardResult.getHolder(),
                                    Processing.DAVR_MASTER_CARD,
                                    cardResult.getPhone(),
                                    c.getToken(),
                                    true,
                                    Currency.UZS
                            );
                            var card = cardDAO.create(preparedCard);
                    loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO, true);

                            if (!cards
                                    .getCards()
                                    .contains(c.getPan())) {
                                cards.getCards().add(c.getPan());
                            }
                        }
                );


    }


    private static void removeAlreadyHasCards(Loan loan, List<MasterCustomerCardsResponse> tokens) {
        log.info("Checking already added master cards from list: {}", tokens);

        if (tokens.isEmpty())
            return;

        var loanCardsList = new ArrayList<>(loan
                .getLoanCards()
                .stream()
                .filter(z -> z.getCard().getProcessing().equals(Processing.DAVR_MASTER_CARD))
                .map(z -> z.getCard().getToken())
                .toList());


        log.info("Loan: {} has {} MASTER cards", loan.getExtId(), loanCardsList.size());


        tokens
                .removeIf(
                        u -> loanCardsList
                                .stream()
                                .anyMatch(s -> u.getToken().equals(s))
                );

        log.info("New MASTER cards after removing: {}", tokens);
    }


    private static void removeAlreadyHasCards4Guarantor(Guarantor guarantor, List<MasterCustomerCardsResponse> tokens) {
        log.info("Checking already added master cards from list: {}", tokens);

        if (tokens.isEmpty())
            return;

        var guarantorCardsList = new ArrayList<>(guarantor
                .getGuarantorCards()
                .stream()
                .filter(z -> z.getCard().getProcessing().equals(Processing.DAVR_MASTER_CARD))
                .map(z -> z.getCard().getToken())
                .toList());


        log.info("Guarantor: {} has {} MASTER cards", guarantor.getPersonCode(), guarantorCardsList.size());


        tokens
                .removeIf(
                        u -> guarantorCardsList
                                .stream()
                                .anyMatch(s -> u.getToken().equals(s))
                );

        log.info("Guarantor new MASTER cards after removing: {}", tokens);
    }

    private void baseGuarantorCardSaverMaster(Guarantor guarantor, CardFinderResponseDTO cards, List<MasterCustomerCardsResponse> tokenList) {

        removeAlreadyHasCards4Guarantor(guarantor, tokenList);

        if (tokenList.isEmpty()) {
            log.warn("Guarantor MASTER cards is empty and will be exited");
            return;
        }


        tokenList
                .forEach(c -> {
                            var cardResponse = fetchMasterCardBalance(c);

                            if (!cardResponse.getStatus())
                                return;

                            var cardResult = cardResponse.getData();

                            saveGuarantorCard(guarantor, c, cardResult);

                            if (!cards
                                    .getCards()
                                    .contains(c.getPan())) {
                                cards.getCards().add(c.getPan());
                            }
                        }
                );


    }

    private SimpleResponse<MasterBalanceResponse> fetchMasterCardBalance(MasterCustomerCardsResponse c) {
        log.info("Validate card: {} and save", c.getPan());

        var balanceRequest = new MasterBalanceRequest(
                c.getToken()
        );
        var masterCardResponse = masterClientService.balance(balanceRequest);

        if (!masterCardResponse.getStatus()) {
            log.error("Card: {} takes error while fetching and will be returned", c.getToken());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), masterCardResponse.getError().getMessage());
        }

        if (masterCardResponse.getData().getError() != null) {
            log.error("Card: {} takes error from BPC and will be returned", c.getToken());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), masterCardResponse.getData().getError().getMessage());
        }

        var cardResult = masterCardResponse.getData().getResult();
        return new SimpleResponse<>(cardResult);
    }

    private void saveGuarantorCard(Guarantor guarantor, MasterCustomerCardsResponse c, MasterBalanceResponse cardResult) {
        var preparedCard = new Card(
                c.getPan(),
                "1249",
                cardResult.getHolder(),
                Processing.DAVR_MASTER_CARD,
                cardResult.getPhone(),
                c.getToken(),
                true,
                Currency.UZS
        );
        var card = cardDAO.create(preparedCard);

        var pk = new GuarantorCardKey(
                guarantor.getId(),
                card.getId()
        );

        var guarantorCard = new GuarantorCard(
                pk,
                guarantor,
                card,
                Payer.GUARANTOR,
                AddType.AUTO
        );

        guarantorCardDAO.save(guarantorCard);
    }

    private void findGuarantorCardsByPINFL(Guarantor guarantor, CardFinderResponseDTO cards) {
        log.info("Begin find guarantor cards by pinfl: {}", guarantor.getPersonCode());

        var validateResult = validateCardFinderResponse(guarantor.getPersonCode());
        if (!validateResult.getStatus())
            return;

        var result = validateResult.getData();

        baseGuarantorCardSaverMaster(guarantor, cards, result);
        log.info("Ended find MASTER cards...");
    }

    private SimpleResponse<List<MasterCustomerCardsResponse>> validateCardFinderResponse(String personCode) {

        if (personCode == null || personCode.isEmpty() || !personCode.matches("\\d{14}")) {
            var message = "PINFL is not valid: %s and will be exited".formatted(personCode);
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var request = new MasterCustomerCardsByPINFLRequest(
                personCode
        );

        var cardFindResponse = masterClientService.customerCardsByPersonCode(request);

        if (!cardFindResponse.getStatus()) {
            log.error("Master card find method taken an error and will be exited");
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), cardFindResponse.getError().getMessage());
        }

        if (cardFindResponse.getData().getError() != null) {
            log.error("BPC connect result is un success: {}", cardFindResponse.getData().getError().getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), cardFindResponse.getData().getError().getMessage());
        }

        var result = cardFindResponse.getData().getResult();

        if (result.isEmpty()) {
            String message = "Client doesn't have any cards from MASTER BPC!";
            log.warn(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        result
                .removeIf(
                        z -> !z.getToken().matches(CardUtils.regexDavrMasterCardToken())
                );

        return new SimpleResponse<>(result);
    }

    public ResultData<CardFinderResponseDTO> getGuarantorCardFinderResponse(Guarantor guarantor) {
        log.info("Starting card find for guarantor: {}", guarantor.getPersonCode());

        var cards = new CardFinderResponseDTO();
        findGuarantorCardsByPINFL(guarantor, cards);
        log.info("Finished master card finder for : {}", guarantor.getPersonCode());

        return ResultData.ok(cards);
    }

}
