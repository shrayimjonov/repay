package uz.repay.card.service.business.adapter.impl.infoAdapterV2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.adapter.CardBalanceFetchRequest;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.dto.master.request.MasterBalanceRequest;
import uz.repay.card.model.dto.master.request.MasterP2PInfoRequest;
import uz.repay.card.service.business.adapter.MasterInfoAdapterV2Service;
import uz.repay.card.service.business.component.DisableCardsComponent;
import uz.repay.card.service.web.MasterClientService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterInfoAdapterV2ServiceImpl implements MasterInfoAdapterV2Service {
    private final MasterClientService masterClientService;
    private final DisableCardsComponent disableCardsComponent;

    @Override
    public SimpleResponse<List<LoanCardsResult>> getCardsBalance(List<CardBalanceFetchRequest> cardsList) {
        log.info("Fetch {} Master cards balance", cardsList.size());

        if (cardsList.isEmpty()) {
            log.info("Request list size empty and will be exited from Cards Balance method!");
            return new SimpleResponse<>(List.of());
        }

        var responseList = fetchCardsBalanceInParallel(cardsList);

        log.info("Master cards fetch balance finished");
        return new SimpleResponse<>(responseList);
    }

    private List<LoanCardsResult> fetchCardsBalanceInParallel(List<CardBalanceFetchRequest> cardsList) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var tasks = cardsList
                    .stream()
                    .map(card -> CompletableFuture.supplyAsync(() -> fetchCardBalance(card), executor))
                    .toList();

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
            return tasks
                    .stream()
                    .map(CompletableFuture::join)
                    .flatMap(Optional::stream)
                    .toList();
        }
    }

    private Optional<LoanCardsResult> fetchCardBalance(CardBalanceFetchRequest z) {
        if (!z.getType().equals(CardType.token)) {
            log.error("Master card type must be token! Now: {}", z.getType());
            return Optional.empty();
        }

        if (!z.getProcessing().equals(Processing.DAVR_MASTER_CARD)) {
            log.error("Processing must be Master! Now: {}", z.getProcessing());
            return Optional.empty();
        }

        if (!z.getCard().matches(CardUtils.regexDavrMasterCardToken())) {
            log.error("Master card: {} doesn't match to token regex!", z.getCard());
            return Optional.empty();
        }

        var result = masterClientService.balance(
                new MasterBalanceRequest(z.getCard())
        );

        if (!result.getStatus()) {
            log.error("Master card: {} fetching error: {} and not added to result list", z.getCard(), result);
            return Optional.empty();
        }

        if (result.getData().getError() != null) {
            log.error("Master card: {} result is FAILED and not added to result list", z.getCard());
            disableCardsComponent.setNotActiveCardAndSave(z.getCardId());
            return Optional.of(new LoanCardsResult(z.getBaseCard()));
        }

        LoanCardsResult loanCardsResult = new LoanCardsResult(z.getCardId(), result.getData().getResult());
        log.info("Prepared loan card result: {}", loanCardsResult);

        return Optional.of(loanCardsResult);
    }

    @Override
    public SimpleResponse<LoanCardsResult> getOneCardBalance(CardBalanceFetchRequest request) {
        String card = request.getCard();

        log.info("Get one Master card balance: {}", card);

        if (!request.getType().equals(CardType.token)) {
            var message = "Master card type must be token! Now: %s".formatted(request.getType());
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }


        if (!request.getProcessing().equals(Processing.DAVR_MASTER_CARD)) {
            var message = "Processing must be Master! Now: %s".formatted(request.getProcessing());
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }


        if (!card.matches(CardUtils.regexDavrMasterCardToken())) {
            var message = "Master card: %s doesn't match to token regex!".formatted(card);
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var response = masterClientService.balance(
                new MasterBalanceRequest(card)
        );

        if (!response.getStatus()) {
            log.error("Master card: {} balance fetching error: {}", card, response.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), response.getError().getMessage());
        }

        if (response.getData().getError() != null) {
            log.error("Master card: {} get balance result is FAILED", card);
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), response.getData().getError().getMessage());
        }

        LoanCardsResult loanCardsResult = new LoanCardsResult(request.getCardId(), response.getData().getResult());
        log.info("Prepared loan card result: {}", loanCardsResult);

        return new SimpleResponse<>(loanCardsResult);
    }


    @Override
    public SimpleResponse<CardShortInfoDTO> takeP2PInfo(String pan) {
        log.info("Take Master: {} p2p info", pan);

        if (!pan.matches(CardUtils.regexMasterCardDAVR())) {
            var message = "Master card: %s doesn't match to pan regex!".formatted(pan);
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var request = new MasterP2PInfoRequest(
                pan
        );


        var response = masterClientService.p2pInfo(request);

        if (!response.getStatus()) {
            log.error("Master card: {} p2pInfo fetching error: {}", pan, response.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), response.getError().getMessage());
        }

        if (response.getData().getError() != null) {
            log.error("Master card: {} p2pInfo result is FAILED", pan);
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), response.getData().getError().getMessage());
        }

        CardShortInfoDTO cardShortInfoDTO = new CardShortInfoDTO(
                response.getData().getResult()
        );
        log.info("Prepared p2pInfo result: {}", cardShortInfoDTO);

        return new SimpleResponse<>(cardShortInfoDTO);
    }


    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.DAVR_MASTER_CARD);
    }
}
