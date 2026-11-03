package uz.repay.card.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.core.request.card.CardSaverParamDTO;
import uz.repay.card.model.dto.core.response.card.CardCollectorDTO;
import uz.repay.card.model.dto.dm2.response.DM2CardsByPinflResponse;
import uz.repay.card.model.dto.elma.response.ElmaVisaCardFindResponse;
import uz.repay.card.model.dto.nciBilling.response.NCICardsByPersonCodeResponse;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.ElmaCardService;
import uz.repay.card.service.business.bean.CardBeanRegistrar;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.card.service.core.component.MasterCardAdderComponent;
import uz.repay.card.service.web.DM2Service;
import uz.repay.card.service.web.NCIBillingService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.utils.CardCommon;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class FirstTimeCardFinderComponent {
    private final NCIBillingService nciBillingService;
    private final DM2Service dm2Service;
    private final MasterCardAdderComponent masterCardAdderComponent;
    private final LoanDAO loanDAO;
    private final CardBeanRegistrar cardBeanRegistrar;
    private final ElmaCardService elmaCardService;


    @Deprecated
    public void findCardFirstTimeAfterLoanAdded(String loanExtId) {
        log.info("Begin first time card finder after loan added by extId: {}", loanExtId);

        var loanOptional = loanDAO.findLoanOptionalByExtId(loanExtId);

        if (loanOptional.isEmpty()) {
            log.error("Loan with extId {} not found", loanExtId);
            return;
        }

        var loan = loanOptional.get();

        if (!loan.getPinfl().matches("\\d{14}")) {
            log.error("Loan PINFL: {} not matched to regex", loan.getPinfl());
            return;
        }

        checkMasterCards(loan);

        checkVisaCards(loan);

        checkCardsFromExternalSystems(loan);

    }

    private void checkCardsFromExternalSystems(Loan loan) {
        handleDavrMobileCards(loan);
        collectABSCardsToMap(loan);
    }

    private void handleDavrMobileCards(Loan loan) {
        log.info("Start find cards from Davr Mobile 2.0...");
        var dm2Result = dm2Service.getClientCardsByPINFL(loan.getPinfl());

        if (!dm2Result.getIsSuccess()) {
            log.error("Davr Mobile 2.0 card find result is FAILED with error: {}", dm2Result.getError());
            return;
        }

        collectCardsByTypeAndSave(
                loan,
                dm2Result
                        .getResult()
                        .stream()
                        .map(DM2CardsByPinflResponse::getCardToken)
                        .toList()
        );

    }

    private void collectCardsByTypeAndSave(Loan loan, List<String> cardsList) {
        var map = cardsList
                .stream()
                .map(this::validateCardPCAndType)
                .filter(SimpleResponse::getStatus)
                .map(SimpleResponse::getData)
                .map(card -> new AbstractMap.SimpleEntry<>(card.getProcessing(), card)) // Создаём пары (cardToken, Processing)
                // Фильтруем только успешные
                .collect(Collectors.groupingBy(
                        AbstractMap.SimpleEntry::getKey, // Группируем по Processing
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()) // Собираем список cardCollectors
                ));

        map
                .forEach((k, v) -> {
                    var result = cardBeanRegistrar.getCardSaveServiceByPC(k);

                    if (!result.getStatus()) {
                        log.error("Can't fetch: {} cards. Cause: {}", k, result.getError());
                        return;
                    }

                    var service = result.getData();

                    v
                            .stream()
                            .map(card -> new AbstractMap.SimpleEntry<>(card.getCardType(), card.getCard()))
                            .collect(Collectors.groupingBy(
                                    AbstractMap.SimpleEntry::getKey, // Группируем по Processing
                                    Collectors.mapping(Map.Entry::getValue, Collectors.toList()) // Собираем список cardCollectors
                            ))
                            .forEach((i, j) -> {
                                switch (i) {
                                    case CardType.pan: {
                                        service.saveCardByPan(
                                                new CardSaverParamDTO<>(loan, j, false, null)
                                        );
                                        break;
                                    }
                                    case CardType.token: {
                                        service.saveCardByToken(
                                                new CardSaverParamDTO<>(loan, j, false, null)
                                        );
                                        break;
                                    }
                                    default: {
                                        var message = "Invalid type: %s for saving!".formatted(i);
                                        log.error(message);
                                        break;
                                    }
                                }
                            });


                });
    }

    private void checkMasterCards(Loan loan) {
        masterCardAdderComponent.getCardFinderResponse(loan);
    }


    private void collectABSCardsToMap(Loan loan) {
        log.info("Start find cards from ABS...");
        var absResult = nciBillingService.getCardsByPersonCode(loan.getPinfl());

        if (absResult.getResult() == null || absResult.getResult().isEmpty()) {
            log.error("ABS card find result is FAILED with error: {}", absResult.getError());
            return;
        }


        collectCardsByTypeAndSave(
                loan,
                absResult
                        .getResult()
                        .stream()
                        .map(NCICardsByPersonCodeResponse::getPan)
                        .toList()
        );
    }

    public SimpleResponse<CardCollectorDTO> validateCardPCAndType(String card) {
        log.info("Start validate card by Processing and Type...");


        var type = CardType.getCardType(card);

        var processingResult = type == CardType.pan ?
                CardCommon.detectProcessingByPan(card) : CardCommon.detectProcessingByToken(card);

        if (!processingResult.getStatus())
            return new SimpleResponse<>(processingResult.getError().getCode(), processingResult.getError().getMessage());


        var dto = new CardCollectorDTO(
                type,
                processingResult.getData(),
                card
        );

        log.info("Return card collector dto: {}", dto);
        return new SimpleResponse<>(dto);
    }


    private void checkVisaCards(Loan loan) {
        log.info("Start find cards from VISA PC...");

        var elmaResult = elmaCardService.findClientVisaCardsByCodes(loan.getPinfl());

        if (!elmaResult.getStatus())
            return;

        if (elmaResult.getData().isEmpty()) {
            log.error("Visa cards is empty(");
            return;
        }


        collectCardsByTypeAndSave(
                loan,
                elmaResult
                        .getData()
                        .stream()
                        .map(ElmaVisaCardFindResponse.ElmaVisaCardResult::getCardNum)
                        .toList()
        );


    }

}
