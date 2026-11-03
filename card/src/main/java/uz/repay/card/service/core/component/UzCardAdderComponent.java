package uz.repay.card.service.core.component;

import feign.FeignException;
import feign.RetryableException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.card.model.dto.core.request.card.CardFinderRequestDTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.dm2.response.DM2CardsByPinflResponse;
import uz.repay.card.model.dto.paidRequest.request.PaidEventDTO;
import uz.repay.card.model.dto.svgate.request.params.SVGateAutoPayCardsParam;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;
import uz.repay.card.model.dto.uzcard.request.CheckCard2PinflRequest;
import uz.repay.card.model.dto.uzcard.request.CheckCardsByPhoneRequest;
import uz.repay.card.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.uzcard.response.result.CardsListToken;
import uz.repay.card.model.entity.*;
import uz.repay.card.model.entity.pk.GuarantorCardKey;
import uz.repay.card.service.core.*;
import uz.repay.card.service.web.DM2Service;
import uz.repay.card.service.web.DMService;
import uz.repay.card.service.web.NCIBillingService;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.*;
import uz.repay.common.utils.BranchUtils;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class UzCardAdderComponent {
    private final UzCardService uzCardService;
    private final DMService dmService;
    private final DM2Service dm2Service;
    private final CardDAO cardDAO;
    private final LoanDAO loanDAO;
    private final LoanCardDAO loanCardDAO;
    private final NCIBillingService nciBillingService;
    private final AutoPayDAO autoPayDAO;
    private final EPosDAO ePosDAO;
    private final ApplicationEventPublisher eventPublisher;
    private final GuarantorCardDAO guarantorCardDAO;

    public ResultData<CardFinderResponseDTO> findUzCardAndAdd(@NonNull CardFinderRequestDTO requestDTO) {
        log.info("Find Humo Card by Loan: {}", requestDTO.getLoanExtId());
        var loan = loanDAO.findLoanByExtId(requestDTO.getLoanExtId());

        return getCardFinderResponse(loan);
    }

    public ResultData<CardFinderResponseDTO> getCardFinderResponse(Loan loan) {

        var cards = new CardFinderResponseDTO();

        if (loan.getPinfl() == null || loan.getPinfl().isEmpty()) {
            log.error("Loan Debtor PINFL is null");
            return ResultData.ok(cards);
        }

        handleDavrMobile1Cards(loan, cards);
        log.info("Cards after DM1: {}", cards);

        handleDavrMobile2Cards(loan, cards);
        log.info("Cards after DM2: {}", cards);

        getUzCardsByPhone(loan, cards);
        log.info("Cards after byPhone: {}", cards);

        getUzCardsFromABS(loan, cards);
        log.info("Cards after ABS: {}", cards);

        try {
            getAutoPayCardsV3(loan, cards);
        } catch (Exception e) {
            log.info("Get autopay cards error: {}", e.getMessage());
        }
        log.info("Cards after AutoPay: {}", cards);

        log.info("Finished uzCard card finder...");
        return new ResultData<>(cards);
    }

    private void handleDavrMobile1Cards(Loan loan, CardFinderResponseDTO cards) {
        log.info("Start Davr Mobile 1.0 card finder for loan: {}", loan.getExtId());

        var response = dmService.getClientCardsByPINFL(loan.getPinfl());

        log.info("Card finder Mobile response: {}", response);

        if (response.getError() != null) {
            return;
        }

        if (response.getResult().getTokens().isEmpty())
            return;


        baseCardSaver(loan, cards, response.getResult().getTokens(), Processing.UZCARD);

        log.info("Finished get Cards from Davr Mobile 1.0");
    }

    private void baseCardSaver(Loan loan, CardFinderResponseDTO cards, ArrayList<String> tokenList, Processing processing) {
        removeAlreadyHasCards(loan, tokenList);

        //remove huMo cards from List
        var afterHandledTokens = getOnlyUzCards(tokenList);

        if (afterHandledTokens.isEmpty())
            return;

        //remove cards which opened to another Person Code
        afterHandledTokens
                .removeIf(t -> !checkCardBelongToPinfl(t, loan.getPinfl()));

        if (afterHandledTokens.isEmpty())
            return;

        var newCardsResponse = getCardsInfo(afterHandledTokens);


        if (newCardsResponse.getError() != null) {
            log.error("Cards Info has error: {}", newCardsResponse.getError().getMessage());
            return;
        }


        if (newCardsResponse.getData().getError() != null) {
            log.error("Cards Info has error result: {}", newCardsResponse.getData().getError().getMessage());
            return;
        }


        newCardsResponse
                .getData()
                .getResult()
                .forEach(n -> {
                            var card = cardDAO.create(n, true, processing);
                            loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO, true);
                        }
                );

        newCardsResponse
                .getData()
                .getResult()
                .stream()
                .map(CardBaseResult::getPan)
                .forEach(n -> {
                    if (!cards
                            .getCards()
                            .contains(n))
                        cards.getCards().add(n);
                });

    }

    private void handleDavrMobile2Cards(Loan loan, CardFinderResponseDTO cards) {
        log.info("Start Davr Mobile 2.0 card finder for loan: {}", loan.getExtId());

        var response = dm2Service.getClientCardsByPINFL(loan.getPinfl());

        log.info("Card finder Mobile response: {}", response);

        if (response.getError() != null) {
            return;
        }

        var allCards = new ArrayList<>(response
                .getResult()
                .stream()
                .map(DM2CardsByPinflResponse::getCardToken)
                .toList());

        if (allCards.isEmpty()) {
            log.info("Davr Mobile 2.0 cards is empty");
            return;
        }


        baseCardSaver(loan, cards, allCards, Processing.UZCARD);

        log.info("Finished get Cards from Davr Mobile 2.0");
    }

    private static void removeAlreadyHasCards(Loan loan, ArrayList<String> cards) {
        if (cards.isEmpty())
            return;

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
                                .anyMatch(u::equals)
                );

        log.info("New cards after removing: {}", cards);
    }

    private static void removeAlreadyHasAutoPayCards(Set<LoanCard> loanCards, List<SVGateAutoPayCardsResult> cards) {
        var loanCardsList = new ArrayList<>(
                loanCards
                        .stream()
                        .map(z -> z.getCard().getToken())
                        .toList());

        if (cards.isEmpty())
            return;

        String loanExtId = loanCards.stream().findAny().map(l -> l.getLoan().getExtId()).orElse("Unknown");
        log.info("Loan: {} has {} cards", loanExtId, loanCardsList.size());


        cards
                .removeIf(
                        u -> loanCardsList
                                .stream()
                                .anyMatch(z -> u.getToken().equals(z))
                );

        log.info("New auto pay cards after removing: {}", cards);
    }

    private Boolean checkCardBelongToPinfl(String token, String personCode) {
        var response = uzCardService.checkCardIsBelongToPersonCode(
                new CheckCard2PinflRequest(token, personCode)
        );

        log.info("Response: {}", response);

        if (response.getError() != null)
            return false;

        if (response.getResult() == null)
            return false;

        return response.getResult().getCode().equals(0);
    }


    private ArrayList<String> getOnlyUzCards(List<String> cards) {
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


    public void getUzCardsByPhone(Loan loan, CardFinderResponseDTO cards) {
        log.info("Started get uzCards by phone method by phone: {} for loan: {}", loan.getPayerPhone(), loan.getExtId());

        var phone = loan.getPayerPhone();

        if (phone == null || phone.isEmpty() || !phone.matches("\\d{12}") || phone.substring(3).matches("^([0-9])\\1*$")) {
            log.info("Phone: {} doesn't match rules and will be returned", phone);
            return;
        }

        var cardsResult = uzCardService.getCardsByPhone(
                new CheckCardsByPhoneRequest(
                        phone
                )
        );


        if (cardsResult.getError() != null) {
            log.info("get uzCards by phone throws error: {}", cardsResult.getError());
            return;
        }


        if (cardsResult.getResult().getError() != null) {
            log.info("get uzCards by phone returned error: {}", cardsResult.getResult().getError());
            return;
        }


        if (cardsResult.getResult().getResult().isEmpty()) {
            log.info("cards is empty, exit method >>");
            return;
        }


        var tokenList = new ArrayList<>(
                cardsResult.getResult().getResult()
                        .stream()
                        .map(CardsListToken::getId)
                        .toList()
        );

        baseCardSaver(loan, cards, tokenList, Processing.UZCARD);

        log.info("Finished get uzCards by phone method");
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

        var cardTokens = new ArrayList<String>();

        absCardsResponse
                .getResult()
                .removeIf(z -> !z.getPan().matches(CardUtils.regexUzCard()));

        log.info("Found {} uz cards", absCardsResponse.getResult().size());

        absCardsResponse
                .getResult()
                .forEach(c -> {
                            var request = new P2PInfoRequest(c.getPan());

                            var p2pInfoResponse = uzCardService.getP2PInfo(request);

                            if (p2pInfoResponse.getError() != null) {
                                log.info("P2P info result not success: {}", p2pInfoResponse);
                                return;
                            }
                            cardTokens.add(p2pInfoResponse.getResult().getId());
                        }
                );


        baseCardSaver(loan, cardFinderResponseDTO, cardTokens, Processing.UZCARD);

        log.info("Finished get uzCards from ABS method");
    }


    public ResultData<CardFinderResponseDTO> getAutoPayCards(Loan loan) {
        log.info("Begin check auto pay cards for loan: {}", loan.getExtId());
        var cards = new CardFinderResponseDTO();

        var loanInfo = nciBillingService.getLoanInfo(loan.getExtId());

        if (loanInfo.getError() != null) {
            log.error(loanInfo.getError().getMessage());
            return ResultData.ok(cards);
        }

        var autoPayOptional = autoPayDAO.findByLoanExtId(loan.getExtId());


        if (autoPayOptional.isPresent()) {
            // var autoPay = autoPayOptional.get();
            log.warn("AutoPay for: {} is already has!", loan.getExtId());
            return ResultData.ok(cards);
        }


        var ePosList = ePosDAO.getAll();

        var ePosOptional = ePosList
                .stream()
                .filter(z -> z.getProcessing().equals(Processing.SVGATE_AUTOPAY)
                        && z.getService().equals(ServiceType.REPAY)
                        && z.getBranch().equals(loan.getBranch()))
                .findFirst();

        if (ePosOptional.isEmpty()) {
            log.error("EPos not found for: {} {}", Processing.SVGATE_AUTOPAY, loan.getBranch());
            return ResultData.ok(cards);
        }

        var ePos = ePosOptional.get();

        var request = new SVGateAutoPayCardsParam(
                loan,
                loanInfo.getResult(),
                ePos
        );

        var autoPay = autoPayBuilder(request);

        var autoPayCardsResult = uzCardService.getAutoPayCards(request);

        if (autoPayCardsResult.getError() != null) {
            log.error(autoPayCardsResult.getError().getMessage());
            return ResultData.ok(cards);
        }

        if (autoPayCardsResult
                .getResult()
                .getError() != null) {
            log.error(autoPayCardsResult.getResult().getError().getMessage());
            return ResultData.ok(cards);
        }


        cards
                .setCards(
                        autoPayCardsResult
                                .getResult()
                                .getResult()
                                .stream().map(SVGateAutoPayCardsResult::getToken)
                                .toList()
                );

        autoPayCardsResult
                .getResult()
                .getResult()
                .forEach(c -> {
                    var autoPayCard = new AutoPayCard(
                            c,
                            autoPay
                    );
                    autoPay.getAutoPayCards().add(autoPayCard);


                });

        autoPayDAO.save(autoPay);

        log.info("End autoPay cards finder for loan: {}", loan.getExtId());
        return ResultData.ok(cards);
    }

    private AutoPay autoPayBuilder(SVGateAutoPayCardsParam request) {
        var preparedAutoPay = new AutoPay(
                request
        );

        return autoPayDAO.save(preparedAutoPay);
    }


    public ResultData<CardFinderResponseDTO> getAutoPayCardsV2(Loan loan, CardFinderResponseDTO cardFinderResponseDTO) {
        log.info("Begin check auto pay cards for loan: {}", loan.getExtId());

        var loanInfo = nciBillingService.getLoanInfo(loan.getExtId());

        if (loanInfo.getError() != null) {
            log.error(loanInfo.getError().getMessage());
            return ResultData.ok(cardFinderResponseDTO);
        }


        var ePosList = ePosDAO.getAll();

        var ePosOptional = ePosList
                .stream()
                .filter(z -> z.getProcessing().equals(Processing.SVGATE_AUTOPAY)
                        && z.getService().equals(ServiceType.REPAY)
                        && z.getBranch().equals(loan.getBranch()))
                .findFirst();

        if (ePosOptional.isEmpty()) {
            log.error("EPos not found for: {} {}", Processing.SVGATE_AUTOPAY, loan.getBranch());
            return ResultData.ok(cardFinderResponseDTO);
        }

        var ePos = ePosOptional.get();

        var request = new SVGateAutoPayCardsParam(
                loan,
                loanInfo.getResult(),
                ePos
        );

        var autoPayCardsResult = uzCardService.getAutoPayCards(request);

        if (autoPayCardsResult.getError() != null) {
            log.error(autoPayCardsResult.getError().getMessage());
            return ResultData.ok(cardFinderResponseDTO);
        }

        if (autoPayCardsResult
                .getResult()
                .getError() != null) {
            log.error(autoPayCardsResult.getResult().getError().getMessage());
            return ResultData.ok(cardFinderResponseDTO);
        }

        var newTokens = new ArrayList<>(autoPayCardsResult.getResult().getResult()
                .stream().map(SVGateAutoPayCardsResult::getToken)
                .toList());

        if (newTokens.isEmpty()) {
            log.info("New tokens is not found");
            return ResultData.ok(cardFinderResponseDTO);
        }

        baseCardSaver(loan, cardFinderResponseDTO, newTokens, Processing.SVGATE_AUTOPAY);

        log.info("End autoPay cards finder for loan: {}", loan.getExtId());
        return ResultData.ok(cardFinderResponseDTO);
    }


    public ResultData<CardFinderResponseDTO> getAutoPayCardsV3(Loan loan, CardFinderResponseDTO cardFinderResponseDTO) {
        log.info("Begin check auto pay cards for loan: {}", loan.getExtId());

        eventPublisher
                .publishEvent(
                        new PaidEventDTO(
                                this.getClass().getSimpleName(),
                                Processing.SVGATE_AUTOPAY,
                                SecurityUtils.getUsernameV2(),
                                loan.getExtId(),
                                Payer.BORROWER
                        )
                );

        var loanInfo = nciBillingService.getLoanInfo(loan.getExtId());

        if (loanInfo.getError() != null) {
            log.error(loanInfo.getError().getMessage());
            return ResultData.ok(cardFinderResponseDTO);
        }


        var ePosList = ePosDAO.getAll();

        var ePosOptional = ePosList
                .stream()
                .filter(z -> z.getProcessing().equals(Processing.SVGATE_AUTOPAY)
                        && z.getService().equals(ServiceType.REPAY)
                        && z.getBranch().equals(loan.getBranch()))
                .findFirst();

        if (ePosOptional.isEmpty()) {
            log.error("EPos not found for: {} {}", Processing.SVGATE_AUTOPAY, loan.getBranch());
            return ResultData.ok(cardFinderResponseDTO);
        }

        var ePos = ePosOptional.get();

        var request = new SVGateAutoPayCardsParam(
                loan,
                loanInfo.getResult(),
                ePos
        );

        var autoPayCardsResult = uzCardService.getAutoPayCards(request);

        if (autoPayCardsResult.getError() != null) {
            log.error(autoPayCardsResult.getError().getMessage());
            return ResultData.ok(cardFinderResponseDTO);
        }

        if (autoPayCardsResult
                .getResult()
                .getError() != null) {
            log.error(autoPayCardsResult.getResult().getError().getMessage());
            return ResultData.ok(cardFinderResponseDTO);
        }

        var newTokens = new ArrayList<>(autoPayCardsResult.getResult().getResult()
                .stream().map(SVGateAutoPayCardsResult::getToken)
                .toList());

        if (newTokens.isEmpty()) {
            log.info("New tokens not found");
            return ResultData.ok(cardFinderResponseDTO);
        }

        autoPayCardsSaver(loan, cardFinderResponseDTO, autoPayCardsResult.getResult().getResult());

        log.info("End autoPay cards finder for loan: {}", loan.getExtId());
        return ResultData.ok(cardFinderResponseDTO);
    }


    public void autoPayCardsSaver(Loan loan, CardFinderResponseDTO cardFinderResponseDTO, List<SVGateAutoPayCardsResult> newTokens) {
//        removeAlreadyHasAutoPayCards(loan, newTokens);

        Set<LoanCard> loanCards = new HashSet<>(loan.getLoanCards());

        if (newTokens.isEmpty()) {
            log.warn("Auto pay cards is empty and will be exit");
            return;
        }


        newTokens
                .forEach(c -> {
                            var preparedCard = new Card(
                                    c.getPan(),
                                    "1249",
                                    c.getFullName(),
                                    Processing.SVGATE_AUTOPAY,
                                    null,
                                    c.getToken(),
                                    true,
                                    Currency.UZS
                            );
                            var card = cardDAO.create(preparedCard);
                            loanCardDAO.createV2(loan, card, Payer.BORROWER, AddType.AUTO, true);
                        }
                );
        removeAlreadyHasAutoPayCards(loanCards, newTokens);

        newTokens
                .stream()
                .map(SVGateAutoPayCardsResult::getPan)
                .forEach(s -> {
                    if (!cardFinderResponseDTO
                            .getCards()
                            .contains(s)) {
                        cardFinderResponseDTO.getCards().add(s);
                    }
                });
    }


    public ResultData<CardFinderResponseDTO> getGuarantorAutoPayCardsV1(
            Guarantor guarantor,
            String loanExtId,
            CardFinderResponseDTO cardFinderResponseDTO
    ) {
        String personCode = guarantor.getPersonCode();

        log.info("Begin check auto pay cards for guarantor: {}", personCode);

        eventPublisher
                .publishEvent(
                        new PaidEventDTO(
                                this.getClass().getSimpleName(),
                                Processing.SVGATE_AUTOPAY,
                                SecurityUtils.getUsernameV2(),
                                loanExtId,
                                Payer.GUARANTOR
                        )
                );

        var loanInfo = nciBillingService.getLoanInfo(loanExtId);

        if (loanInfo.getError() != null) {
            log.error("LoanInfo taken an error: {}", loanInfo.getError().getMessage());
            return ResultData.ok(cardFinderResponseDTO);
        }


        var ePosList = ePosDAO.getAll();

        Branch branch = BranchUtils.getBranchByLoan(loanExtId);

        var ePosOptional = ePosList
                .stream()
                .filter(z -> z.getProcessing().equals(Processing.SVGATE_AUTOPAY)
                        && z.getService().equals(ServiceType.REPAY)
                        && z.getBranch().equals(branch))
                .findFirst();

        if (ePosOptional.isEmpty()) {
            log.error("EPos not found for: {} {}", Processing.SVGATE_AUTOPAY, branch);
            return ResultData.ok(cardFinderResponseDTO);
        }

        var ePos = ePosOptional.get();

        var request = new SVGateAutoPayCardsParam(
                loanExtId,
                guarantor,
                loanInfo.getResult(),
                ePos
        );

        var autoPayCardsResult = uzCardService.getAutoPayCards(request);

        if (autoPayCardsResult.getError() != null) {
            log.error(autoPayCardsResult.getError().getMessage());
            return ResultData.ok(cardFinderResponseDTO);
        }

        if (autoPayCardsResult
                .getResult()
                .getError() != null) {
            log.error(autoPayCardsResult.getResult().getError().getMessage());
            return ResultData.ok(cardFinderResponseDTO);
        }

        var newTokens = new ArrayList<>(autoPayCardsResult.getResult().getResult()
                .stream().map(SVGateAutoPayCardsResult::getToken)
                .toList());

        if (newTokens.isEmpty()) {
            log.info("Guarantor new tokens not found");
            return ResultData.ok(cardFinderResponseDTO);
        }

        autoPayGuarantorCardsSaver(guarantor, cardFinderResponseDTO, autoPayCardsResult.getResult().getResult());

        log.info("End autoPay cards finder for guarantor: {}", guarantor.getPersonCode());
        return ResultData.ok(cardFinderResponseDTO);
    }


    public void autoPayGuarantorCardsSaver(Guarantor guarantor,
                                           CardFinderResponseDTO cardFinderResponseDTO,
                                           List<SVGateAutoPayCardsResult> newTokens) {
        removeAlreadyHasAutoPayCards4Guarantor(guarantor, newTokens);


        if (newTokens.isEmpty()) {
            log.warn("Guarantor auto pay cards is empty and will be exit");
            return;
        }


        newTokens
                .forEach(c -> saveCardToGuarantor(guarantor, c));

        newTokens
                .stream()
                .map(SVGateAutoPayCardsResult::getPan)
                .forEach(s -> {
                    if (!cardFinderResponseDTO
                            .getCards()
                            .contains(s)) {
                        cardFinderResponseDTO.getCards().add(s);
                    }
                });
    }

    private void saveCardToGuarantor(Guarantor guarantor, SVGateAutoPayCardsResult c) {
        var preparedCard = new Card(
                c.getPan(),
                "1249",
                c.getFullName(),
                Processing.SVGATE_AUTOPAY,
                null,
                c.getToken(),
                true,
                Currency.UZS
        );
        var card = cardDAO.create(preparedCard);

        var guarantorCard = new GuarantorCard(
                new GuarantorCardKey(
                        guarantor.getId(),
                        card.getId()),
                guarantor,
                card,
                Payer.GUARANTOR,
                AddType.AUTO
        );
        guarantorCardDAO.save(guarantorCard);
    }

    private static void removeAlreadyHasAutoPayCards4Guarantor(Guarantor guarantor, List<SVGateAutoPayCardsResult> cards) {
        var guarantorCardsList = new ArrayList<>(guarantor
                .getGuarantorCards()
                .stream()
                .map(z -> z.getCard().getToken())
                .toList());

        if (cards.isEmpty())
            return;

        log.info("Guarantor: {} has {} cards", guarantor.getPersonCode(), guarantorCardsList.size());


        cards
                .removeIf(
                        u -> guarantorCardsList
                                .stream()
                                .anyMatch(z -> u.getToken().equals(z))
                );

        log.info("Guarantor new auto pay cards after removing: {}", cards);
    }

}
