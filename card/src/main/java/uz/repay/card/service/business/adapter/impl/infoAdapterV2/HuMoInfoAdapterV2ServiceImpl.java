package uz.repay.card.service.business.adapter.impl.infoAdapterV2;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.adapter.CardBalanceFetchRequest;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humoGateway.response.HuMoBankBins;
import uz.repay.card.service.business.adapter.HuMoInfoAdapterV2Service;
import uz.repay.card.service.business.component.DisableCardsComponent;
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.card.service.web.HuMoGatewayService;
import uz.repay.common.exception.TimeOutException;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HuMoInfoAdapterV2ServiceImpl implements HuMoInfoAdapterV2Service {
    private final HuMoClientService huMoClientService;
    private final DisableCardsComponent disableCardsComponent;
    private final HuMoGatewayService huMoGatewayService;

    @Override
    public SimpleResponse<List<LoanCardsResult>> getCardsBalance(@NotNull List<CardBalanceFetchRequest> cardsList) {
        log.info("Fetch {} HuMo cards balance", cardsList.size());

        if (cardsList.isEmpty()) {
            log.info("Request list size empty and will be exited from Cards Balance method!");
            return new SimpleResponse<>(List.of());
        }

        var responseList = fetchCardsBalanceInParallel(cardsList);

        log.info("HuMo cards fetch balance finished");
        return new SimpleResponse<>(responseList);
    }

    private List<LoanCardsResult> fetchCardsBalanceInParallel(List<CardBalanceFetchRequest> cardsList) {
        int batchSize = 5;
        var responseList = new ArrayList<LoanCardsResult>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int from = 0; from < cardsList.size(); from += batchSize) {
                int to = Math.min(from + batchSize, cardsList.size());
                var batch = cardsList.subList(from, to);
                log.info("Fetching HuMo cards balance batch [{}-{}] of {}", from, to, cardsList.size());
                var tasks = batch
                        .stream()
                        .map(card -> CompletableFuture.supplyAsync(() -> fetchCardBalance(card), executor))
                        .toList();

                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

                tasks.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Optional::stream)
                        .forEach(responseList::add);
            }
        }

        return responseList;
    }

    private Optional<LoanCardsResult> fetchCardBalance(CardBalanceFetchRequest z) {
        if (!z.getType().equals(CardType.pan)) {
            log.error("HuMo card type must be pan! Now: {}", z.getType());
            return Optional.empty();
        }

        if (!z.getProcessing().equals(Processing.HUMO)) {
            log.error("Processing must be HuMo! Now: {}", z.getProcessing());
            return Optional.empty();
        }

        if (!z.getCard().matches(CardUtils.regexHumo())) {
            log.error("HuMo card: {} doesn't match to pan regex!", z.getCard());
            return Optional.empty();
        }

        var result = getCardBalance(z.getCard());
        log.info("HuMo card result: {}", result);

        if (!result.getStatus()) {
            log.error("HuMo card: {} fetching error: {} and not added to result list", z.getCard(), result);
            return Optional.empty();
        }

        if (result.getData().error() != null) {
            log.error("HuMo card: {} result is FAILED and not added to result list", z.getCard());
            setNotActiveCardAndSave(z.getCardId(), result);
            return Optional.of(new LoanCardsResult(z.getBaseCard()));
        }

        LoanCardsResult loanCardsResult = new LoanCardsResult(z.getCardId(), result);
        log.info("Prepared loan card result: {}", loanCardsResult);

        return Optional.of(loanCardsResult);
    }


    private SimpleResponse<HumoCardInfoResponse> getCardBalance(String h) {
        try {
            var result = huMoClientService.getCardInfo(new HumoCardInfoRequest(h));
            return new SimpleResponse<>(result);
        } catch (TimeOutException t) {
            log.error("{} fetch card result has error: {}", h, t.getMessage());
            return new SimpleResponse<>(ResultState.TimeOutError.getMessage(), true);
        } catch (Exception e) {
            log.error("{} fetch card result has error: {}", h, e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    private void setNotActiveCardAndSave(Long key, SimpleResponse<HumoCardInfoResponse> result) {
        if (result.getData().error().getMessage().equals("Card not found")) {
            log.info("Card with id: {} returned \"Card not found\" message in response and will be set not active card", key);
            disableCardsComponent.setNotActiveCardAndSave(key);
        }
    }

    @Override
    public SimpleResponse<LoanCardsResult> getOneCardBalance(@NotNull CardBalanceFetchRequest request) {
        String card = request.getCard();

        log.info("Get one huMo card balance: {}", card);

        if (!request.getType().equals(CardType.pan)) {
            var message = "HuMo card type must be pan! Now: %s".formatted(request.getType());
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }


        if (!request.getProcessing().equals(Processing.HUMO)) {
            var message = "Processing must be HuMo! Now: %s".formatted(request.getProcessing());
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }


        if (!card.matches(CardUtils.regexHumo())) {
            var message = "HuMo card: %s doesn't match to pan regex!".formatted(card);
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var response = getCardBalance(card);

        if (!response.getStatus()) {
            log.error("HuMo card: {} balance fetching error: {}", card, response.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), response.getError().getMessage());
        }

        if (response.getData().error() != null) {
            log.error("HuMo card: {} get balance result is FAILED", card);
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), response.getData().error().getMessage());
        }

        LoanCardsResult loanCardsResult = new LoanCardsResult(request.getCardId(), response);
        log.info("Prepared loan card result: {}", loanCardsResult);

        return new SimpleResponse<>(loanCardsResult);
    }


    @Override
    public SimpleResponse<CardShortInfoDTO> takeP2PInfo(String pan) {
        log.info("Take huMo: {} p2p info", pan);

        if (!pan.matches(CardUtils.regexHumo())) {
            var message = "HuMo card: %s doesn't match to pan regex!".formatted(pan);
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var request = new HumoCardInfoRequest(
                pan
        );

        var binResult = validateBinTypeAndPossibility(pan);

        if (!binResult.getStatus())
            return new SimpleResponse<>(binResult.getError().getCode(), binResult.getError().getMessage());


        var response = huMoClientService.p2pInfo(request);

        if (!response.getStatus()) {
            log.error("HuMo card: {} p2pInfo fetching error: {}", pan, response.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), response.getError().getMessage());
        }

        if (response.getData().getError() != null) {
            log.error("HuMo card: {} p2pInfo result is FAILED", pan);
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), response.getData().getError().getMessage());
        }

        CardShortInfoDTO cardShortInfoDTO = new CardShortInfoDTO(
                response.getData().getResult()
        );
        log.info("Prepared p2pInfo result: {}", cardShortInfoDTO);

        return new SimpleResponse<>(cardShortInfoDTO);
    }

    private SimpleResponse<HuMoBankBins> validateBinTypeAndPossibility(String pan) {
        var binsResult = huMoGatewayService.getBankBins();

        if (!binsResult.getStatus()) {
            log.error("HuMo get bin result throws error: {}", binsResult.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), binsResult.getError().getMessage());
        }

        List<HuMoBankBins> data = binsResult.getData();

        if (data == null || data.isEmpty()) {
            var message = "Bin result is empty!";
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        Optional<HuMoBankBins> huMoBankBinOptional = data
                .stream()
                .filter(b -> pan.contains(b.getBinCode()))
                .findFirst();

        if (huMoBankBinOptional.isEmpty()) {
            String message = "Bin for card: %s not found in huMo bin list".formatted(pan);
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        var huMoBankBin = huMoBankBinOptional.get();

        if (huMoBankBin.getIsPossible()) {
            String message = "Card: %s type is not PERSONAL".formatted(pan);
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        return new SimpleResponse<>(huMoBankBin);
    }


    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.HUMO);
    }
}
