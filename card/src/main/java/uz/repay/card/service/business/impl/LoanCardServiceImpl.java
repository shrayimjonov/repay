package uz.repay.card.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.card.config.aop.CardFounder;
import uz.repay.card.model.dto.core.request.card.CardFinderRequestDTO;
import uz.repay.card.model.dto.core.request.card.CardInfoRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardDeleteDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardsRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardsV2DTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.core.response.card.CardInfoResponseDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.model.entity.LoanCard;
import uz.repay.card.model.enums.PC;
import uz.repay.card.service.business.ElmaCardService;
import uz.repay.card.service.business.LoanCardService;
import uz.repay.card.service.business.adapter.impl.cardSave.VisaCardSaveServiceImpl;
import uz.repay.card.service.core.*;
import uz.repay.card.service.core.component.HumoCardAdderComponent;
import uz.repay.card.service.core.component.MasterCardAdderComponent;
import uz.repay.card.service.core.component.UzCardAdderComponent;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.exception.CardException;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.LoanCardsResponseDTO;
import uz.repay.common.model.dto.response.core.LoansCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.params.LoanCardsParam;
import uz.repay.common.model.enums.*;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanCardServiceImpl implements LoanCardService {
    private final LoanDAO loanDAO;
    private final StatusDAO statusDAO;
    private final LoanCardDAO loanCardDAO;
    private final SwitcherRedisService switcherRedisService;
    private final HumoCardAdderComponent humoCardAdderComponent;
    private final UzCardAdderComponent uzCardAdderComponent;
    private final UzCardService uzCardService;
    private final CardDAO cardDAO;
    private final MasterCardAdderComponent masterCardAdderComponent;
    private final ElmaCardService elmaCardService;
    private final VisaCardSaveServiceImpl visaCardSaveService;

    @Override
    @Deprecated
    public ResultData<LoanCardsResponseDTO<LoanCardsResult>> getLoanCards(@NonNull LoanCardsRequestDTO requestDTO) {

        var loanCards = requestDTO
                .getLoanIds()
                .stream()
                .map(z -> {
                    var lc = getLoanAllCards(z);
                    return new LoanCardsParam<>(z, lc);
                })
                .toList();

        return new ResultData<>(new LoanCardsResponseDTO<>(loanCards));
    }

    @Override
    public ResultData<List<LoanCardsResult>> getLoanAllCards(Long loanId) {
        log.info("Get cards of loanId: {}", loanId);
        var loan = loanDAO.findById(loanId);

        SecurityUtils.checkUserRequestedBranch(loan.getExtId());

        if (loan.getLoanCards().isEmpty()) {
            log.info("0 cards of Loan: {}", loan.getExtId());
            return new ResultData<>(List.of());
        }

        var humoPans = new HashMap<Long, String>();
        var uzCardTokens = new HashMap<Long, String>();
        var autoPayTokens = new HashMap<Long, String>();
        var davrVisaTokens = new HashMap<Long, String>();
        var davrMasterTokens = new HashMap<Long, String>();

        loan
                .getLoanCards()
                .forEach(
                        l -> {
                            if (l.getCard().getIsActive() && l.getCard().getIsConfirmed() && !l.getIsDeleted())
                                if (l.getCard().getProcessing().equals(Processing.HUMO)) {
                                    var switcher = switcherRedisService.findByName(Processing.HUMO.name());
                                    if (switcher.getIsEnabled())
                                        humoPans.put(l.getCard().getId(), getCardDebitAccount(l.getCard()));
                                } else if (l.getCard().getProcessing().equals(Processing.UZCARD)) {
                                    var switcher = switcherRedisService.findByName(Processing.UZCARD.name());
                                    if (switcher.getIsEnabled())
                                        uzCardTokens.put(l.getCard().getId(), getCardDebitAccount(l.getCard()));
                                } else if (l.getCard().getProcessing().equals(Processing.DAVR_VISA)) {
                                    var switcher = switcherRedisService.findByName(Processing.DAVR_VISA.name());
                                    if (switcher.getIsEnabled())
                                        davrVisaTokens.put(l.getCard().getId(), getCardDebitAccount(l.getCard()));
                                } else if (l.getCard().getProcessing().equals(Processing.SVGATE_AUTOPAY)) {
                                    var switcher = switcherRedisService.findByName(Processing.SVGATE_AUTOPAY.name());
                                    if (switcher.getIsEnabled())
                                        autoPayTokens.put(l.getCard().getId(), getCardDebitAccount(l.getCard()));
                                } else if (l.getCard().getProcessing().equals(Processing.DAVR_MASTER_CARD)) {
                                    var switcher = switcherRedisService.findByName(Processing.DAVR_MASTER_CARD.name());
                                    if (switcher.getIsEnabled())
                                        davrMasterTokens.put(l.getCard().getId(), getCardDebitAccount(l.getCard()));
                                } else
                                    log.warn("Processing type {} get Card Balance method not found", l.getCard().getProcessing());
                            else
                                log.warn("Card: {} is not active and not sent to Processing", l.getCard().getMaskedPan());
                        }
                );


        var allCardParams = new HashMap<PC, HashMap<Long, String>>();
        allCardParams.put(PC.HUMO, humoPans);
        allCardParams.put(PC.UZCARD, uzCardTokens);
        allCardParams.put(PC.DAVR_VISA, davrVisaTokens);
        allCardParams.put(PC.AUTOPAY, autoPayTokens);
        allCardParams.put(PC.DAVR_MASTER_CARD, davrMasterTokens);

        var cards = mergeProcessingResults(allCardParams);

        cards.sort(Comparator.comparing(LoanCardsResult::getState));

        var statusList = statusDAO.getAll();

        loan.getLoanCards()
                .forEach(
                        s -> {
                            if (cards.stream().noneMatch(z -> z.getId().equals(s.getCard().getId())))
                                if (!s.getCard().getIsActive())
                                    cards.add(new LoanCardsResult(s));
                                else
                                    cards.add(new LoanCardsResult(s, switcherRedisService.findByName(s.getCard().getProcessing().name()).getIsEnabled()));


                            cards
                                    .forEach(z -> {
                                                if (s.getCard().getId().equals(z.getId())) {
                                                    z.setPayer(s.getPayer());
                                                    z.setIpAddress(s.getIpAddress());
                                                }

                                                statusList
                                                        .forEach(
                                                                d -> {
                                                                    if (d.getCode().equals(z.getState().toString())
                                                                            && z.getProcessing().equals(d.getProcessing())
                                                                            && d.getType().equals(StatusType.CARD))
                                                                        z.setStateDefinition(d.getDefinitionUz());
                                                                }
                                                        );
                                            }
                                    );
                        }

                );


        return new ResultData<>(cards);
    }

    private ArrayList<LoanCardsResult> mergeProcessingResults(HashMap<PC, HashMap<Long, String>> cardParams) {
        var cards = new ArrayList<LoanCardsResult>();

        cardParams
                .forEach((key, value) -> {
                    switch (key) {
                        case HUMO -> {
                            var pc = PC.HUMO.cardService();
                            cards.addAll(pc.getCardsBalance(value));
                        }
                        case UZCARD -> {
                            var pc = PC.UZCARD.cardService();
                            var response = pc.getCardsBalance(value);
                            cards.addAll(response);

                        }
                        case DAVR_VISA -> {
                            var pc = PC.DAVR_VISA.cardService();
                            var response = pc.getCardsBalance(value);
                            cards.addAll(response);
                        }
                        case AUTOPAY -> {
                            var pc = PC.AUTOPAY.cardService();
                            var response = pc.getCardsBalance(value);
                            cards.addAll(response);
                        }
                        case DAVR_MASTER_CARD -> {
                            var pc = PC.DAVR_MASTER_CARD.cardService();
                            var response = pc.getCardsBalance(value);
                            cards.addAll(response);
                        }
                        default -> throw new CardException("Incorrect PC type: %s".formatted(key));
                    }
                });

        return cards;
    }


    @Override
    @Deprecated
    public ResultData<CoreError> deleteCardFromLoan(@NonNull LoanCardDeleteDTO dto) {
        log.info("Received command to delete card from loan with request: {}", dto);
        var card = cardDAO.findById(dto.getCardId());

        var loan = loanDAO.findById(dto.getLoanId());
        loanCardDAO.deleteOrApprove(loan, dto.getCardId(), true);

//        if (card.getProcessing().equals(Processing.SVGATE_AUTOPAY))
//            uzCardService.autoPaySubscribe(card.getToken(), false);

        var result = new ResultData<>(new CoreError(0, ResponseStatus.SUCCESS.name()));

        log.info("Finished command to delete card from loan with response: {}", result);
        return result;
    }

    @Override
    public ResultData<CoreError> approveCardInLoan(@NonNull LoanCardDeleteDTO dto) {
        log.info("Received command to approve card from loan with request: {}", dto);
        var card = cardDAO.findById(dto.getCardId());

        var loan = loanDAO.findById(dto.getLoanId());
        loanCardDAO.deleteOrApprove(loan, dto.getCardId(), false);

        // TODO clearify from uzcard
//        if (card.getProcessing().equals(Processing.SVGATE_AUTOPAY))
//            uzCardService.autoPaySubscribe(card.getToken(), true);

        var result = new ResultData<>(new CoreError(0, ResponseStatus.SUCCESS.name()));

        log.info("Finished command to approve card from loan with response: {}", result);
        return result;
    }

    @Override
    public ResultData<CardInfoResponseDTO> getCardInfo(@NonNull CardInfoRequestDTO dto) {
        var loanCard = loanCardDAO.findByLoanAndCard(dto.getLoanId(), dto.getCardId());

        return loanCard
                .map(card -> new ResultData<>(new CardInfoResponseDTO(card)))
                .orElseGet(
                        () -> new ResultData<>(
                                ResultState.EntityNotFound,
                                "LoanId: %d with cardId: %d not found in database".formatted(dto.getLoanId(), dto.getCardId()
                                )
                        )
                );

    }

    @Override
    @CardFounder
    @Deprecated
    public ResultData<CardFinderResponseDTO> findLoanCards(@NonNull CardFinderRequestDTO requestDTO) {
        log.info("Find Cards by Loan: {}", requestDTO.getLoanExtId());
        var newCards = checkCardsFromPCs(requestDTO);


        log.info("Finished get card finder response with cards size: {}", newCards.size());
        return new ResultData<>(new CardFinderResponseDTO(newCards));
    }

    @SuppressWarnings("all")
    private synchronized ArrayList<String> checkCardsFromPCs(CardFinderRequestDTO requestDTO) {

        var loan = loanDAO.findLoanByExtId(requestDTO.getLoanExtId());

        var newCards = new ArrayList<String>();


        log.info("<<< Starting huMo adder >>>");
        var huMoAdder = humoCardAdderComponent.getCardFinderResponse(loan);
        newCards.addAll(huMoAdder.getResult().getCards());

        log.info("<<< Starting uzCard DavrMobile adder >>>");
        var uzCardAdder = uzCardAdderComponent.getCardFinderResponse(loan);
        newCards.addAll(uzCardAdder.getResult().getCards());

        var masterAdder = masterCardAdderComponent.getCardFinderResponse(loan);
        newCards.addAll(masterAdder.getResult().getCards());

        return newCards;

    }

    @Override
    public ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCardsV2(@NonNull LoanCardsV2DTO requestDTO) {
        var allUzCards = new ArrayListValuedHashMap<Loan, Card>();
        var allAutoPayCards = new ArrayListValuedHashMap<Loan, Card>();
        var allHuMoCards = new ArrayListValuedHashMap<Loan, Card>();
        var davrVISACards = new ArrayListValuedHashMap<Loan, Card>();
        var davrMasterCards = new ArrayListValuedHashMap<Loan, Card>();

        var loanList = loanDAO.findByExtIdsAndNotDeleted(requestDTO.getLoanIds());

        if (loanList.isEmpty())
            return new ResultData<>(ResultState.EntityNotFound, "Loan List is empty");

        loanList
                .forEach(z -> {
                    log.info("Get internal request loan cards for : {}", z.getExtId());
                    z.getLoanCards()
                            .forEach(
                                    s -> {
                                        if (s.getCard().getIsActive() && s.getCard().getIsConfirmed() && !s.getIsDeleted())
                                            detectPCAndGetBalance(z, s, allUzCards, allHuMoCards, davrVISACards, allAutoPayCards, davrMasterCards);
                                        else
                                            log.warn("Card: {} is not active and not sent to Processing", s.getCard().getMaskedPan());
                                    }
                            );
                });


        var allCardParams = handlePCToMap(allUzCards, allHuMoCards, davrVISACards, allAutoPayCards, davrMasterCards);

        var cards = mergeProcessingResults(allCardParams);

        cards
                .removeIf(z -> z.getDebitParams() == null);

        var resultList = new ArrayList<LoansCardsResponseDTO<LoanCardsResult>>();
        loanList
                .forEach(
                        s -> {
                            s.getLoanCards()
                                    .forEach(
                                            sc -> cards.forEach(cc -> {
                                                if (cc.getId().equals(sc.getCard().getId())) {
                                                    cc.setPayer(sc.getPayer());
                                                    var optionalRs = resultList.stream().filter(r -> r.getLoanExtId().equals(s.getExtId())).findFirst();

                                                    if (optionalRs.isEmpty())
                                                        resultList.add(new LoansCardsResponseDTO<>(s.getExtId(), cc));
                                                    else
                                                        optionalRs.get().getCards().add(cc);
                                                }

                                            })

                                    );
                            log.info("Loan: {} fetched {} cards", s.getExtId(), cards.size());
                        }

                );

        return new ResultData<>(resultList);
    }

    private void detectPCAndGetBalance(Loan z, LoanCard s,
                                       ArrayListValuedHashMap<Loan, Card> allUzCards,
                                       ArrayListValuedHashMap<Loan, Card> allHuMoCards,
                                       ArrayListValuedHashMap<Loan, Card> davrVisaCards,
                                       ArrayListValuedHashMap<Loan, Card> autoPayCards,
                                       ArrayListValuedHashMap<Loan, Card> davrMasterCards) {

        switch (s.getCard().getProcessing()) {
            case UZCARD -> handleUzCardPC(z, s, allUzCards);
            case HUMO -> handleHuMoPC(z, s, allHuMoCards);
            case DAVR_VISA -> handleDavrVISAPC(z, s, davrVisaCards);
            case SVGATE_AUTOPAY -> handleAutoPayPC(z, s, autoPayCards);
            case DAVR_MASTER_CARD -> handleDavrMasterPC(z, s, davrMasterCards);

            default -> log.error("Card type: {} not detected PC", s.getCard().getProcessing());
        }

    }

    private void handleHuMoPC(Loan z, LoanCard s, ArrayListValuedHashMap<Loan, Card> allHuMoCards) {
        if (switcherRedisService.findByName(Processing.HUMO.name()).getIsEnabled()) {
            if (!s.getPayer().equals(Payer.EMPLOYEE))
                allHuMoCards.put(z, s.getCard());
            else
                log.info("Card: {} payer is EMPLOYEE and not add to debit cards list", s.getCard().getMaskedPan());
        } else
            log.warn("HUMO switcher is turned off");
    }

    private void handleUzCardPC(Loan z, LoanCard s, ArrayListValuedHashMap<Loan, Card> allUzCards) {
        if (switcherRedisService.findByName(Processing.UZCARD.name()).getIsEnabled()) {
            if (!s.getPayer().equals(Payer.EMPLOYEE))
                allUzCards.put(z, s.getCard());
            else
                log.info("Card: {} payer is EMPLOYEE and not add to debit cards list", s.getCard().getMaskedPan());
        } else
            log.warn("UZCARD switcher is turned off");
    }

    private void handleAutoPayPC(Loan z, LoanCard s, ArrayListValuedHashMap<Loan, Card> allUzCards) {
        if (switcherRedisService.findByName(Processing.SVGATE_AUTOPAY.name()).getIsEnabled()) {
            if (!s.getPayer().equals(Payer.EMPLOYEE))
                allUzCards.put(z, s.getCard());
            else
                log.info("Card: {} payer is EMPLOYEE and not add to debit cards list", s.getCard().getMaskedPan());
        } else
            log.warn("SVGATE_AUTOPAY switcher is turned off");
    }

    private void handleDavrVISAPC(Loan z, LoanCard s, ArrayListValuedHashMap<Loan, Card> allDavrVisaCards) {
        if (switcherRedisService.findByName(Processing.DAVR_VISA.name()).getIsEnabled()) {
            if (!s.getPayer().equals(Payer.EMPLOYEE))
                allDavrVisaCards.put(z, s.getCard());
            else
                log.info("Card: {} payer is EMPLOYEE and not add to debit cards list", s.getCard().getMaskedPan());
        } else
            log.warn("Davr VISA switcher is turned off");
    }

    private HashMap<PC, HashMap<Long, String>> handlePCToMap(ArrayListValuedHashMap<Loan, Card> allUzCards,
                                                             ArrayListValuedHashMap<Loan, Card> allHuMoCards,
                                                             ArrayListValuedHashMap<Loan, Card> allDavrVisaCards,
                                                             ArrayListValuedHashMap<Loan, Card> allAutoPayCards,
                                                             ArrayListValuedHashMap<Loan, Card> allDavrMasterCards) {
        var uzCards = allUzCards
                .values()
                .stream()
                .distinct()
                .collect(Collectors.toMap(
                        Card::getId,
                        this::getCardDebitAccount
                ));

        var autoPayCards = allAutoPayCards
                .values()
                .stream()
                .distinct()
                .collect(Collectors.toMap(
                        Card::getId,
                        this::getCardDebitAccount
                ));


        var huMoCards = allHuMoCards
                .values()
                .stream()
                .distinct()
                .collect(Collectors.toMap(
                        Card::getId,
                        this::getCardDebitAccount
                ));

        var davrVisaCards = allDavrVisaCards
                .values()
                .stream()
                .distinct()
                .collect(Collectors.toMap(
                        Card::getId,
                        this::getCardDebitAccount
                ));

        var davrMasterCards = allDavrMasterCards
                .values()
                .stream()
                .distinct()
                .collect(Collectors.toMap(
                        Card::getId,
                        this::getCardDebitAccount
                ));

        var allCardParams = new HashMap<PC, HashMap<Long, String>>();

        allCardParams.put(PC.HUMO, new HashMap<>(huMoCards));
        allCardParams.put(PC.UZCARD, new HashMap<>(uzCards));
        allCardParams.put(PC.DAVR_VISA, new HashMap<>(davrVisaCards));
        allCardParams.put(PC.AUTOPAY, new HashMap<>(autoPayCards));
        allCardParams.put(PC.DAVR_MASTER_CARD, new HashMap<>(davrMasterCards));

        return allCardParams;
    }

    public String getCardDebitAccount(Card card) {
        var pc = card.getProcessing();
        if (pc.equals(Processing.UZCARD) || pc.equals(Processing.SVGATE_AUTOPAY))
            return card.getToken();
        else if (pc.equals(Processing.HUMO))
            return CardUtils.decryptPan(card.getPan());
        else if (pc.equals(Processing.DAVR_VISA))
            return CardUtils.decryptPan(card.getPan());
        else if (pc.equals(Processing.DAVR_MASTER_CARD))
            return card.getToken();
        else
            throw new CardException("Card type: %s not defined PC".formatted(pc));
    }

    private void handleDavrMasterPC(Loan z, LoanCard s, ArrayListValuedHashMap<Loan, Card> allCards) {
        if (switcherRedisService.findByName(Processing.DAVR_MASTER_CARD.name()).getIsEnabled()) {
            if (!s.getPayer().equals(Payer.EMPLOYEE))
                allCards.put(z, s.getCard());
            else
                log.info("Card: {} payer is EMPLOYEE and not add to debit cards list", s.getCard().getMaskedPan());
        } else
            log.warn("UZCARD switcher is turned off");
    }
}
