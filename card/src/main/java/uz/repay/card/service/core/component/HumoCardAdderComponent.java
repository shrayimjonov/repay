package uz.repay.card.service.core.component;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.card.model.dto.core.request.card.CardFinderRequestDTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humo.response.HumoCardsByPINFLResponse;
import uz.repay.card.model.dto.nciBilling.response.NCICardsByPersonCodeResponse;
import uz.repay.card.model.dto.paidRequest.request.PaidEventDTO;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.card.service.web.NCIBillingService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Deprecated
public class HumoCardAdderComponent {
    private final HuMoClientService huMoClientService;
    private final LoanCardDAO loanCardDAO;
    private final LoanDAO loanDAO;
    private final CardDAO cardDAO;
    private final ApplicationEventPublisher eventPublisher;
    private final NCIBillingService nciBillingService;

    public HumoCardAdderComponent(HuMoClientService huMoClientService,
                                  LoanCardDAO loanCardDAO,
                                  LoanDAO loanDAO,
                                  CardDAO cardDAO, ApplicationEventPublisher eventPublisher, NCIBillingService nciBillingService) {

        this.huMoClientService = huMoClientService;
        this.loanCardDAO = loanCardDAO;
        this.loanDAO = loanDAO;
        this.cardDAO = cardDAO;
        this.eventPublisher = eventPublisher;
        this.nciBillingService = nciBillingService;
    }

    public ResultData<CardFinderResponseDTO> findHUMOCardAndAdd(@NonNull CardFinderRequestDTO requestDTO) {
        log.info("Find Humo Card by Loan: {}", requestDTO.getLoanExtId());
        var loan = loanDAO.findLoanByExtId(requestDTO.getLoanExtId());

        return getCardFinderResponse(loan);
    }

    @Deprecated
    public ResultData<CardFinderResponseDTO> getCardFinderResponse(Loan loan) {
        // # turned of cause is very cheap each request to huMo

        // return checkAndHandleCards(loan);
        var cards = new CardFinderResponseDTO();
        getUzCardsFromABS(loan, cards);
        log.info("Finished huMo card finder...");

        return ResultData.ok(cards);
    }

    @Deprecated
    public ResultData<CardFinderResponseDTO> checkAndHandleCards(Loan loan) {
        eventPublisher
                .publishEvent(
                        new PaidEventDTO(
                                this.getClass().getSimpleName(),
                                Processing.HUMO,
                                SecurityUtils.getUsernameV2(),
                                loan.getExtId(),
                                Payer.BORROWER
                        )
                );

        if (loan.getPinfl() == null || loan.getPinfl().isEmpty()) {
            log.error("Loan Debtor PINFL is null");
            return new ResultData<>(new CardFinderResponseDTO());
        }


        var humoCards = getClientCardsByPINFL(loan.getPinfl());
        log.error("HuMo Cards response: {}", humoCards);

        if (humoCards.getError() != null)
            return new ResultData<>(new CardFinderResponseDTO());


        removeAlreadyHasCards(loan, humoCards);

        var cardsList = new ArrayList<String>();

        humoCards
                .getData()
                .getData()
                .forEach(h -> {
                    var response = getHumoCardBalance(h.getCard());

                    if (response.getStatus()) {

                        if (response.getData().error() != null) {
                            log.error("HuMo fetching error: {}", response.getData().error());
                            return;
                        }

                        var card = cardDAO.create(response.getData(), true);
                        loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO, true);
                        cardsList.add(h.getCard());
                    }
                });

        return new ResultData<>(new CardFinderResponseDTO(cardsList));
    }

    @Deprecated
    public SimpleResponse<HumoCardsByPINFLResponse.PINFLResult> getClientCardsByPINFL(String PINFL) {
        try {
            var response = huMoClientService.getClientCardsByPINFL(PINFL);

            if (response.getResult() != null)
                return new SimpleResponse<>(response.getResult());
            else
                return new SimpleResponse<>(400, response.getError().getMessage());

        } catch (Exception e) {
            log.error("Get Client Cards By PINFL method error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    private static void removeAlreadyHasCards(Loan loan, SimpleResponse<HumoCardsByPINFLResponse.PINFLResult> humoCards) {
        var loanCardsList = new ArrayList<>(loan
                .getLoanCards()
                .stream()
                .filter(z -> z.getCard().getProcessing().equals(Processing.HUMO))
                .map(lc -> CardUtils.decryptBulkPan(lc.getCard().getPan()))
                .toList());

        log.info("Loan: {} has {} cards", loan.getExtId(), loanCardsList.size());


        humoCards
                .getData()
                .getData()
                .removeIf(
                        hc -> loanCardsList
                                .stream()
                                .anyMatch(lc -> hc.getCard().equals(lc))
                );

        log.info("New cards after removing: {}", humoCards.getData().getData());
    }

    private SimpleResponse<HumoCardInfoResponse> getHumoCardBalance(String card) {
        try {
            var response = huMoClientService.getCardInfo(new HumoCardInfoRequest(card));
            if (response.result() != null)
                return new SimpleResponse<>(response);
            else
                return new SimpleResponse<>(400, response.error().getMessage());
        } catch (Exception e) {
            log.info("Card: {} info occurred error: {}", card, e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }


    private ArrayList<String> getOnlyHuMoCards(List<String> cards) {
        var newList = new ArrayList<String>();

        cards
                .forEach(
                        c -> {
                            if (c.matches(CardUtils.regexHumo()))
                                newList.add(c);
                        }

                );

        log.info("Only huMo cards after removing: {}", newList);
        return newList;
    }


    private static void removeAlreadyHasHuMoCards(Loan loan, ArrayList<String> cards) {
        if (cards.isEmpty())
            return;

        var loanCardsList = new ArrayList<>(loan
                .getLoanCards()
                .stream()
                .filter(z -> z.getCard().getProcessing().equals(Processing.HUMO))
                .map(z -> CardUtils.decryptBulkPan(z.getCard().getPan()))
                .toList());


        log.info("Loan: {} has {} huMo cards", loan.getExtId(), loanCardsList.size());


        cards
                .removeIf(
                        u -> loanCardsList
                                .stream()
                                .anyMatch(u::equals)
                );

        log.info("New huMo cards after removing: {}", cards);
    }

    private void baseCardSaverHuMo(Loan loan, CardFinderResponseDTO cards, ArrayList<String> panList, Processing processing) {
        if (!processing.equals(Processing.HUMO)) {
            log.warn("Processing is not huMo and will be returned!");
            return;
        }

//        removeAlreadyHasHuMoCards(loan, panList);

        //remove huMo cards from List
        var afterHandledPans = getOnlyHuMoCards(panList);

        if (afterHandledPans.isEmpty())
            return;

        //remove cards which opened to another Person Code
        var newCardsResponse = new ArrayList<HumoCardInfoResponse>();

        afterHandledPans
                .forEach(h -> {
                            var response = getHumoCardBalance(h);
                            if (response.getStatus())
                                newCardsResponse.add(response.getData());
                        }
                );


        if (newCardsResponse.isEmpty()) {
            log.warn("New cards list is empty after getBalance method");
            return;
        }


        newCardsResponse
                .forEach(n -> {
                            var card = cardDAO.create(n, true);
                            loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO,true);
                        }
                );

        newCardsResponse
                .stream()
                .map(z -> z.result().card().primaryAccountNumber())
                .forEach(n -> {
                    if (!cards
                            .getCards()
                            .contains(n))
                        cards.getCards()
                                .add(CardUtils.maskPanDigits(n));
                });

    }


    public void getUzCardsFromABS(Loan loan, CardFinderResponseDTO cardFinderResponseDTO) {
        log.info("Get uzCards from ABS for loan: {} by pinfl: {}", loan.getExtId(), loan.getPinfl());

        var personCode = loan.getPinfl();

        if (personCode == null || personCode.isEmpty()) {
            log.info("Pinfl: {} and will be exited from getUzCardsFromAbs method!", personCode);
            return;
        }


        var absCardsResponse = nciBillingService.getCardsByPersonCode(personCode);


        if (!absCardsResponse.getStatus().equals(ResponseStatus.SUCCESS)) {
            log.info("cards by person code ABS response not success: {}", absCardsResponse);
            return;
        }

        if (absCardsResponse.getResult().isEmpty()) {
            log.info("cards by person code ABS response is empty");
            return;
        }

        absCardsResponse
                .getResult()
                .removeIf(z -> !z.getPan().matches(CardUtils.regexHumo()));

        var panList = new ArrayList<>(absCardsResponse
                .getResult()
                .stream()
                .map(NCICardsByPersonCodeResponse::getPan)
                .toList());


        log.info("Found {} huMo cards", absCardsResponse.getResult().size());

        baseCardSaverHuMo(loan, cardFinderResponseDTO, panList, Processing.HUMO);

        log.info("Finished taking huMo cards process from ABS method...");
    }
}
