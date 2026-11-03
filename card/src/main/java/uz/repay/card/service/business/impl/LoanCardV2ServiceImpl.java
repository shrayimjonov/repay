package uz.repay.card.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.card.config.aop.CardFounder;
import uz.repay.card.model.dto.adapter.CardBalanceFetchRequest;
import uz.repay.card.model.dto.core.request.card.CardFinderRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardToggleRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardsV2DTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.model.entity.base.BaseCardEntity;
import uz.repay.card.service.business.LoanCardV2Service;
import uz.repay.card.service.business.bean.CardBeanRegistrar;
import uz.repay.card.service.business.component.CardFinderComponent;
import uz.repay.card.service.business.component.LogProtocolComponent;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.card.service.core.StatusDAO;
import uz.repay.card.service.core.SwitcherRedisService;
import uz.repay.card.util.ServiceUtils;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.LoansCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanCardV2ServiceImpl implements LoanCardV2Service {
    private final LoanDAO loanDAO;
    private final SwitcherRedisService switcherRedisService;
    private final CardBeanRegistrar cardBeanRegistrar;
    private final StatusDAO statusDAO;
    private final LogProtocolComponent logProtocolComponent;
    private final LoanCardDAO loanCardDAO;
    private final CardFinderComponent cardFinderComponent;

    @Override
    public ResultData<List<LoanCardsResult>> getLoanAllCards(Long loanId) {
        log.info("Received command to get loan cards by loan id: {}", loanId);

        var loanResult = findLoan(loanId);

        if (!loanResult.getStatus())
            return new ResultData<>(loanResult.getError(), false);

        var loan = loanResult.getData();

        var cardsResult = getLoanCardsBalance(loan);

        if (!cardsResult.getStatus())
            return new ResultData<>(cardsResult.getError(), false);

        var cards = cardsResult.getData();

        var statusList = statusDAO.getAll();

        cards
                .forEach(
                        z -> statusList
                                .forEach(
                                        d -> {
                                            if (d.getCode().equals(z.getState().toString())
                                                    && z.getProcessing().equals(d.getProcessing())
                                                    && d.getType().equals(StatusType.CARD))
                                                z.setStateDefinition(d.getDefinitionUz());
                                        }
                                ));


        loan.getLoanCards()
                .forEach(
                        s -> cards
                                .forEach(z -> {
                                            if (s.getCard().getId().equals(z.getId())) {
                                                z.setPayer(s.getPayer());
                                                if (z.getIpAddress() == null)
                                                    z.setIpAddress(s.getIpAddress());
                                            }
                                        }
                                )
                );

        loan.getGuarantors()
                .stream()
                .flatMap(z -> z.getGuarantor().getGuarantorCards().stream())
                .forEach(f -> cards
                        .forEach(z -> {
                                    if (f.getCard().getId().equals(z.getId())) {
                                        z.setPayer(f.getPayer());
                                        if (z.getIpAddress() == null)
                                            z.setIpAddress(f.getIpAddress());
                                        z.setGuarantorId(f.getId().getGuarantorId());
                                    }
                                }
                        ));

        return new ResultData<>(cards);
    }


    private SimpleResponse<ArrayList<LoanCardsResult>> getLoanCardsBalance(Loan loan) {
        log.info("Get loan: {} cards balance", loan.getExtId());


        SecurityUtils.checkUserRequestedBranch(loan.getExtId());

        var cardsMap = new HashMap<Processing, List<CardBalanceFetchRequest>>();
        var notHandledCards = new ArrayList<LoanCardsResult>();

        if (!loan.getLoanCards().isEmpty())
            loan
                    .getLoanCards()
                    .forEach(l -> collectCards(l, notHandledCards, cardsMap));

        if (!loan.getGuarantors().isEmpty())
            loan
                    .getGuarantors()
                    .stream()
                    .filter(z -> !z.getIsDeleted())
                    .flatMap(z -> z.getGuarantor().getGuarantorCards().stream())
                    .forEach(g -> collectCards(g, notHandledCards, cardsMap));


        var cards = mergeProcessingResults(cardsMap);
        cards.addAll(notHandledCards); // add not handled cards
        cards.sort(
                Comparator
                        .comparing(LoanCardsResult::getState, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(LoanCardsResult::getPan, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(LoanCardsResult::getHolder, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        return new SimpleResponse<>(cards);
    }

    private void collectCards(BaseCardEntity b,
                              ArrayList<LoanCardsResult> notHandledCards,
                              Map<Processing, List<CardBalanceFetchRequest>> cardsMap) {
        if (b.getCard().getIsActive() && !b.getIsDeleted() && b.getIsConfirmed()) {
            Processing processing = b.getCard().getProcessing();

            var switcher = switcherRedisService.findByName(processing.name());

            if (!switcher.getIsEnabled()) {
                log.error("Card not added to balance request: reason=switcher_disabled, processing={}, {}", processing, summarizeBaseCard(b));
                notHandledCards.add(new LoanCardsResult(b, switcher.getIsEnabled()));
                return;
            }

            cardRequestBuilder(b, processing, cardsMap);
        } else {
            log.error("Card not added to balance request: reason=inactive_deleted_or_not_confirmed, {}", summarizeBaseCard(b));
            notHandledCards.add(new LoanCardsResult(b, true));
        }
    }


    private void cardRequestBuilder(BaseCardEntity l, Processing processing, Map<Processing, List<CardBalanceFetchRequest>> map) {
        Card card = l.getCard();

        var cardAccountResult = ServiceUtils.takeDebitAccount(card);

        if (!cardAccountResult.getStatus()) {
            log.error("Card debit account could not be resolved: cardId={}, maskedPan={}, processing={}, error={}",
                    card.getId(), card.getMaskedPan(), processing, cardAccountResult.getError());
            return;
        }

        var cardAccount = cardAccountResult.getData();

        var cardTypeResult = ServiceUtils.detectTypeByCard(cardAccount);

        if (!cardTypeResult.getStatus()) {
            log.error("Card type could not be detected for balance request: cardId={}, maskedPan={}, processing={}, account={}, error={}",
                    card.getId(), card.getMaskedPan(), processing, summarizeSecureValue(cardAccount), cardTypeResult.getError());
            return;
        }

        var cardType = cardTypeResult.getData();


        var cardRequest = new CardBalanceFetchRequest(
                l,
                cardType,
                cardAccount
        );


        map.computeIfAbsent(processing, ignored -> new ArrayList<>()).add(cardRequest);

    }


    private ArrayList<LoanCardsResult> mergeProcessingResults(Map<Processing, List<CardBalanceFetchRequest>> cardsBalanceRequest) {
        var cards = new ArrayList<LoanCardsResult>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var tasks = cardsBalanceRequest
                    .entrySet()
                    .stream()
                    .map(entry -> CompletableFuture
                            .supplyAsync(() -> takeBalance(entry.getKey(), entry.getValue()), executor)
                            .exceptionally(ex -> {
                                log.error("Occurred error while computable future with message: {} - returning cards without balance", ex.getMessage());
                                return balanceUnavailableResults(entry.getValue());
                            }))
                    .toList();

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
            tasks.forEach(task -> cards.addAll(task.join()));
        }

        return cards;
    }

    private List<LoanCardsResult> takeBalance(Processing k, List<CardBalanceFetchRequest> v) {
        var cardAdapterResult = cardBeanRegistrar.getCardInfoV2AdapterByPC(k);

        if (!cardAdapterResult.getStatus()) {
            log.error("Card adapter result has error, returning cards without balance: processing={}, requestCount={}, error={}, requests={}",
                    k, v.size(), cardAdapterResult.getError().getMessage(), summarizeRequests(v));
            return balanceUnavailableResults(v);
        }

        var service = cardAdapterResult.getData();

        var serviceResult = service.getCardsBalance(v);

        if (!serviceResult.getStatus()) {
            log.error("Get Balance result has error, returning cards without balance: processing={}, requestCount={}, error={}, requests={}",
                    k, v.size(), serviceResult.getError().getMessage(), summarizeRequests(v));
            return balanceUnavailableResults(v);
        }


        var balanceResult = serviceResult.getData();

        if (balanceResult.isEmpty()) {
            log.warn("Get Balance result is empty, returning cards without balance: processing={}, requestCount={}, requests={}",
                    k, v.size(), summarizeRequests(v));
            return balanceUnavailableResults(v);
        }

        return fillMissingCards(k, v, balanceResult);
    }

    private List<LoanCardsResult> fillMissingCards(Processing k,
                                                   List<CardBalanceFetchRequest> requests,
                                                   List<LoanCardsResult> balanceResult) {
        var returnedCardIds = balanceResult
                .stream()
                .map(LoanCardsResult::getId)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        var missing = requests
                .stream()
                .filter(request -> request.getCardId() != null && !returnedCardIds.contains(request.getCardId()))
                .toList();

        if (missing.isEmpty())
            return balanceResult;

        log.warn("Balance response is partial, returning missing cards without balance: processing={}, requested={}, returned={}, missing={}",
                k, requests.size(), balanceResult.size(), summarizeRequests(missing));

        var combined = new ArrayList<>(balanceResult);
        missing.forEach(request -> combined.add(LoanCardsResult.balanceUnavailable(request.getBaseCard())));
        return combined;
    }

    private static List<LoanCardsResult> balanceUnavailableResults(List<CardBalanceFetchRequest> requests) {
        return requests
                .stream()
                .filter(request -> request.getBaseCard() != null)
                .map(request -> LoanCardsResult.balanceUnavailable(request.getBaseCard()))
                .toList();
    }


    private SimpleResponse<Loan> findLoan(Long loanId) {
        var loanOptional = loanDAO.findOptionalById(loanId);

        if (loanOptional.isEmpty()) {
            var message = "Loan with id: %d not found".formatted(loanId);
            log.error(message);
            return new SimpleResponse<>(ResultState.EntityNotFound.getCode(), "Method finished with error: %s".formatted(message));
        }

        var loan = loanOptional.get();

        return new SimpleResponse<>(loan);
    }

    @Override
    public ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCards(LoanCardsV2DTO requestDTO) {
        log.info("Received command to get loans cards with request dto: {}", requestDTO);
        var loanList = loanDAO.findByExtIdsAndNotDeleted(requestDTO.getLoanIds());

        if (loanList.isEmpty()) {
            var message = "Loan List is empty";
            log.error("Finished command to get loans cards with error: {}", message);
            return new ResultData<>(ResultState.EntityNotFound, message);
        }
        var cardsMap = new HashMap<Processing, List<CardBalanceFetchRequest>>();
        var notHandledCards = new ArrayList<LoanCardsResult>();

        loanList
                .forEach(c -> c
                        .getLoanCards()
                        .stream()
                        .filter(loanCard -> loanCard.getCard().getIsActive() && !loanCard.getIsDeleted())
                        .filter(f -> !f.getPayer().equals(Payer.EMPLOYEE))
                        .forEach(l -> collectCards(l, notHandledCards, cardsMap)));

        loanList
                .forEach(l -> l.getGuarantors()
                        .stream()
                        .filter(s -> !s.getIsDeleted())
                        .flatMap(z -> z.getGuarantor().getGuarantorCards().stream())
                        .filter(guarantorCard -> guarantorCard.getCard().getIsActive() && !guarantorCard.getIsDeleted())
                        .forEach(g -> collectCards(g, notHandledCards, cardsMap)));

        cardsMap.forEach((processing, requests) ->
                log.info("Balance request group: processing={}, count={}, requests={}", processing, requests.size(), summarizeRequests(requests)));

        var cards = mergeProcessingResults(cardsMap);

        var noDebitParams = cards
                .stream()
                .filter(z -> z.getDebitParams() == null)
                .toList();
        if (!noDebitParams.isEmpty())
            log.warn("Cards returned without debitParams in internal loansCards response: count={}, cards={}",
                    noDebitParams.size(), summarizeResults(noDebitParams));

        if (!notHandledCards.isEmpty()) {
            log.warn("Not handled cards are merged into internal loansCards response without balance: count={}, cards={}",
                    notHandledCards.size(), summarizeResults(notHandledCards));
            cards.addAll(notHandledCards);
        }

        var cardsById = groupCardsById(cards);
        var resultByLoanExtId = new LinkedHashMap<String, LoansCardsResponseDTO<LoanCardsResult>>();

        loanList
                .forEach(
                        s -> {
                            s.getLoanCards()
                                    .forEach(addCardsToResponse(s.getExtId(), cardsById, resultByLoanExtId));
                            log.info("Loan: {} fetched {} cards", s.getExtId(), cards.size());

                            s.getGuarantors().stream()
                                    .flatMap(z -> z.getGuarantor().getGuarantorCards().stream())
                                    .forEach(addCardsToResponse(s.getExtId(), cardsById, resultByLoanExtId));
                        }

                );

        var resultList = new ArrayList<>(resultByLoanExtId.values());
        var result = ResultData.ok(resultList);
        log.info("Finished command to get loans cards with response size: {}", result.getResult().size());
        return result;
    }

    private static Map<Long, List<LoanCardsResult>> groupCardsById(List<LoanCardsResult> cards) {
        var cardsById = new HashMap<Long, List<LoanCardsResult>>();
        cards.forEach(card -> cardsById.computeIfAbsent(card.getId(), ignored -> new ArrayList<>()).add(card));
        return cardsById;
    }

    private static Consumer<BaseCardEntity> addCardsToResponse(String loanExtId,
                                                               Map<Long, List<LoanCardsResult>> cardsById,
                                                               Map<String, LoansCardsResponseDTO<LoanCardsResult>> resultByLoanExtId) {
        return gc -> {
            var matchedCards = cardsById.get(gc.getCard().getId());
            if (matchedCards == null) {
                if (isExpectedInResponse(gc))
                    log.warn("No balance result matched response card: loanExtId={}, {}", loanExtId, summarizeBaseCard(gc));
                return;
            }

            matchedCards.forEach(cc -> {
                cc.setPayer(gc.getPayer());
                var response = resultByLoanExtId.get(loanExtId);

                if (response == null)
                    resultByLoanExtId.put(loanExtId, new LoansCardsResponseDTO<>(loanExtId, cc));
                else
                    response.getCards().add(cc);
            });
        };
    }

    private static boolean isExpectedInResponse(BaseCardEntity cardEntity) {
        if (cardEntity == null || cardEntity.getCard() == null)
            return false;

        return Boolean.TRUE.equals(cardEntity.getCard().getIsActive())
                && !Boolean.TRUE.equals(cardEntity.getIsDeleted())
                && !Payer.EMPLOYEE.equals(cardEntity.getPayer());
    }

    private static String summarizeRequests(List<CardBalanceFetchRequest> requests) {
        if (requests == null || requests.isEmpty())
            return "[]";

        return requests
                .stream()
                .limit(10)
                .map(LoanCardV2ServiceImpl::summarizeRequest)
                .toList()
                .toString();
    }

    private static String summarizeRequest(CardBalanceFetchRequest request) {
        if (request == null)
            return "null";

        var card = request.getBaseCard() == null ? null : request.getBaseCard().getCard();
        return "{cardId=%s, maskedPan=%s, processing=%s, type=%s, account=%s, payer=%s}"
                .formatted(
                        request.getCardId(),
                        card == null ? null : card.getMaskedPan(),
                        request.getProcessing(),
                        request.getType(),
                        summarizeSecureValue(request.getCard()),
                        request.getPayer()
                );
    }

    private static String summarizeResults(List<LoanCardsResult> cards) {
        if (cards == null || cards.isEmpty())
            return "[]";

        return cards
                .stream()
                .limit(10)
                .map(card -> "{cardId=%s, pan=%s, processing=%s, state=%s, hasDebitParams=%s, payer=%s}"
                        .formatted(
                                card.getId(),
                                card.getPan(),
                                card.getProcessing(),
                                card.getState(),
                                card.getDebitParams() != null,
                                card.getPayer()
                        ))
                .toList()
                .toString();
    }

    private static String summarizeBaseCard(BaseCardEntity baseCard) {
        if (baseCard == null)
            return "baseCard=null";

        var card = baseCard.getCard();
        if (card == null)
            return "card=null, payer=%s, linkDeleted=%s, linkConfirmed=%s"
                    .formatted(baseCard.getPayer(), baseCard.getIsDeleted(), baseCard.getIsConfirmed());

        return "cardId=%s, maskedPan=%s, processing=%s, cardActive=%s, linkDeleted=%s, linkConfirmed=%s, payer=%s, token=%s"
                .formatted(
                        card.getId(),
                        card.getMaskedPan(),
                        card.getProcessing(),
                        card.getIsActive(),
                        baseCard.getIsDeleted(),
                        baseCard.getIsConfirmed(),
                        baseCard.getPayer(),
                        summarizeSecureValue(card.getToken())
                );
    }

    private static String summarizeSecureValue(String value) {
        if (value == null)
            return null;

        var lastChars = Math.min(6, value.length());
        return "len=%d,last=%s".formatted(value.length(), value.substring(value.length() - lastChars));
    }

    @Override
    public ResultData<CoreError> deleteOrApproveCard(@NotNull LoanCardToggleRequestDTO requestDTO) {
        log.info("Received command to delete/approve card from loan with request dto: {}", requestDTO);
        Long cardId = requestDTO.getCardId();
        Long loanId = requestDTO.getLoanId();

        var loanCardOptional = loanCardDAO.findByLoanAndCard(loanId, cardId);

        if (loanCardOptional.isEmpty()) {
            var message = "LoanCard with loanId: %d and cardId: %d not found".formatted(loanId, cardId);
            log.error(message);
            return new ResultData<>(ResultState.EntityNotFound, message);
        }

        var loanCard = loanCardOptional.get();

        loanCard.setIsDeleted(!loanCard.getIsDeleted());
        loanCardDAO.save(loanCard);

        logProtocolComponent.saveLog(
                loanCard.getLoan().getExtId(),
                ProtocolType.CARD_LOAN_CARD_TOGGLE,
                Map.of(
                        "toggle", loanCard.getIsDeleted(),
                        "loan_id", requestDTO.getLoanId(),
                        "card_id", requestDTO.getCardId()
                )
        );

        var result = new ResultData<>(new CoreError(0, ResponseStatus.SUCCESS.name()));

        log.info("Finished command to delete/approve card from loan with response dto: {}", result);
        return result;
    }

    @Override
    @CardFounder
    public ResultData<CardFinderResponseDTO> findLoanCards(@NonNull CardFinderRequestDTO requestDTO) {
        log.info("Received command to find loan cards with request dto: {}", requestDTO);

        var loanResult = loanDAO.findLoanOptionalByExtId(requestDTO.getLoanExtId());

        if (loanResult.isEmpty()) {
            var message = "LoanExtId: %s is not found".formatted(requestDTO.getLoanExtId());
            log.error("End find card method, cause: {}", message);
            return new ResultData<>(ResultState.DataNotFound, message);
        }

        var loan = loanResult.get();


        cardFinderComponent.findCardsFromAllDetectors(loan);

        var result = ResultData.ok(
                new CardFinderResponseDTO(loan.getPans())
        );

        log.info("Finished command to find loan cards with response dto: {}", result);
        return result;
    }
}
