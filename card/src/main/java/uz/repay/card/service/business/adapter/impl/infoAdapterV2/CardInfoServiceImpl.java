package uz.repay.card.service.business.adapter.impl.infoAdapterV2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.repay.card.client.HuMoMiddlewareFeignClient;
import uz.repay.card.client.SVGateFeignClient;
import uz.repay.card.client.UzCardIntegrationFeignClient;
import uz.repay.card.model.dto.huMoMiddleware.request.HuMoMiddlewareBaseRequest;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoCardV3InfoRequestParam;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequestDTO;
import uz.repay.card.model.dto.humo.response.HuMoCardBaseResponse;
import uz.repay.card.model.dto.humo.response.HumoCardInfoByLoanResponse;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponseList;
import uz.repay.card.model.dto.svgate.request.SVGateBaseRequest;
import uz.repay.card.model.dto.svgate.request.params.SVGateCardsGetParam;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.model.entity.base.BaseCardEntity;
import uz.repay.card.repository.projection.HumoLoanCardProjection;
import uz.repay.card.service.business.adapter.CardInfoService;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.card.util.SystemTool;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Powered by: Shukhratjon Rayimjonov
 * Date: 18.06.2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CardInfoServiceImpl implements CardInfoService {
    private static final int HUMO_CARD_INFO_REQUEST_BATCH_SIZE = 100;

    HuMoMiddlewareFeignClient humoClient;

    UzCardIntegrationFeignClient uzcardClient;

    SVGateFeignClient svgateClient;

    ObjectMapper objectMapper;


    LoanDAO loanDAO;

    CardDAO cardDAO;

    @Override
    public Object cardInfo(Long loanId) {
        Loan loan = loanDAO.findById(loanId);
        Map<Processing, List<Card>> cardMap = loan.getLoanCards().stream().map(BaseCardEntity::getCard).collect(Collectors.groupingBy(Card::getProcessing));
        List<Object> resultList = new ArrayList<>();
        cardMap.forEach((k, v) -> {
            switch (k) {
                case UZCARD ->
                        resultList.add(uzcardCardInfo(new BalanceRequest(new ArrayList<>(v.stream().map(Card::getToken).toList()))));
                case SVGATE_AUTOPAY -> {
                    log.info("SVGate AutoPay cards: {}", loanId);
                    v.forEach(card -> log.info("Card PAN: {}, Decrypt pan: {}, Token: {}", card.getPan(), CardUtils.decryptPan(card.getPan()), card.getToken()));
                    resultList.add(svgateCardInfo(new SVGateBaseRequest<>(
                                    "2.0",
                                    "cards.get",
                                    SystemTool.requestIdGenerator(),
                                    new SVGateCardsGetParam(
                                            new BalanceRequest(new ArrayList<>(v.stream().map(Card::getToken).toList()))
                                    )
                            )
                    ));
                }
                case HUMO -> {
                    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                        List<CompletableFuture<Object>> tasks = v.stream()
                                .map(card -> CompletableFuture
                                        .supplyAsync(() -> humoCardInfo(new HumoCardInfoRequest(CardUtils.decryptPan(card.getPan()))), executor)
                                        .exceptionally(ex -> {
                                            log.error("Humo card info failed for PAN {}: {}", card.getPan(), ex.getMessage());
                                            return null;
                                        }))
                                .toList();

                        List<Object> humoResults = tasks.stream()
                                .map(CompletableFuture::join)
                                .filter(Objects::nonNull)
                                .toList();

                        resultList.add(humoResults);
                    }
                }
            }
        });
        return resultList;
    }

    private Object humoCardInfo(HumoCardInfoRequest request) {
        var originalRequest = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                new HuMoCardV3InfoRequestParam(
                        request
                )
        );
        log.info("Humo middleware Card Info request: {}", originalRequest);
        Object response = humoClient.getCustomerCardInfo(originalRequest);
        try {
            log.info("Humo Card Info Response: {}", objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            log.info("Object writing error: {}", e.getMessage());
        }
        return response;
    }

    private Object uzcardCardInfo(BalanceRequest request) {
        log.info("UzCard Card Info Request: {}", request);

        Object response = uzcardClient.getCardsInfo(request);
        try {
            log.info("UzCard Card Info Response: {}", objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            log.info("Object writing error: {}", e.getMessage());
        }
        return response;
    }


    private Object svgateCardInfo(SVGateBaseRequest<SVGateCardsGetParam> request) {
        log.info("SVGate Card Info Request: {}", request);
        UzCardBaseResponse<List<CardBaseResult>> response = svgateClient.getCardsInfo(request);
        try {
            log.info("SVGate Card Info Response: {}", objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            log.info("Object writing error: {}", e.getMessage());
        }
        return response;
    }

    public ResultData<HumoCardInfoResponseList> getHumoCardsInfoByLoanExtIds(HumoCardInfoRequestDTO requestDTO) {
        if (requestDTO == null || CollectionUtils.isEmpty(requestDTO.loanExtIds())) {
            return ResultData.ok(new HumoCardInfoResponseList(List.of()));
        }

        var requestStartNanos = System.nanoTime();
        var loanExtIds = requestDTO.loanExtIds().stream()
                .filter(Objects::nonNull)
                .filter(loanExtId -> !loanExtId.isBlank())
                .distinct()
                .toList();

        if (loanExtIds.isEmpty()) {
            return ResultData.ok(new HumoCardInfoResponseList(List.of()));
        }

        var resultList = new ArrayList<HumoCardInfoByLoanResponse>();
        for (var loanExtIdBatch : partition(loanExtIds)) {
            resultList.addAll(resolveHumoLoanBatch(loanExtIdBatch));
        }

        var durationMillis = (System.nanoTime() - requestStartNanos) / 1_000_000;
        log.info("Humo card info request completed. requestedLoans: {}, responseGroups: {}, durationMs: {}",
                loanExtIds.size(), resultList.size(), durationMillis);

        return ResultData.ok(new HumoCardInfoResponseList(resultList));
    }

    private List<HumoCardInfoByLoanResponse> resolveHumoLoanBatch(List<String> loanExtIds) {
        var batchStartNanos = System.nanoTime();
        var cardsByLoanExtId = cardDAO.findHumoLoanCardsByLoanExtIds(loanExtIds).stream()
                .collect(Collectors.groupingBy(
                        HumoLoanCardProjection::getLoanExtId,
                        Collectors.toList()
                ));
        var matchedCards = cardsByLoanExtId.values().stream().mapToInt(List::size).sum();

        List<HumoCardInfoByLoanResponse> resultList;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var tasks = loanExtIds.stream()
                    .map(loanExtId -> CompletableFuture.supplyAsync(
                            () -> resolveLoanCardInfo(loanExtId, cardsByLoanExtId.getOrDefault(loanExtId, List.of())),
                            executor
                    ))
                    .toList();

            resultList = tasks.stream()
                    .map(CompletableFuture::join)
                    .toList();
        }

        log.info("Humo card info batch completed. requestedLoans: {}, matchedCards: {}, durationMs: {}",
                loanExtIds.size(), matchedCards,
                (System.nanoTime() - batchStartNanos) / 1_000_000);

        return resultList;
    }

    private List<List<String>> partition(List<String> values) {
        var result = new ArrayList<List<String>>();
        for (int start = 0; start < values.size(); start += CardInfoServiceImpl.HUMO_CARD_INFO_REQUEST_BATCH_SIZE) {
            result.add(values.subList(start, Math.min(values.size(), start + CardInfoServiceImpl.HUMO_CARD_INFO_REQUEST_BATCH_SIZE)));
        }
        return result;
    }

    private String maskPan(String pan) {
        if (pan == null || pan.isBlank()) {
            return null;
        }
        if (pan.matches("\\d{16}")) {
            return CardUtils.maskPanDigits(pan);
        }
        return "****" + pan.substring(Math.max(0, pan.length() - 4));
    }

    private HumoCardInfoByLoanResponse resolveLoanCardInfo(String loanExtId, List<HumoLoanCardProjection> cards) {
        if (CollectionUtils.isEmpty(cards)) {
            return new HumoCardInfoByLoanResponse(loanExtId, List.of());
        }

        var results = cards.stream()
                .map(card -> resolveHumoCardInfo(loanExtId, card))
                .filter(Objects::nonNull)
                .toList();

        return new HumoCardInfoByLoanResponse(loanExtId, results);
    }

    private HumoCardInfoResponse.HumoCardResult resolveHumoCardInfo(String loanExtId, HumoLoanCardProjection card) {
        if (card == null) {
            return null;
        }

        try {
            var pan = CardUtils.decryptPan(card.getPan());
            if (pan.isBlank()) {
                log.warn("event=humo.card-info.lookup.skipped reason=empty_pan loanExtId={} cardId={}",
                        loanExtId, card.getCardId());
                return null;
            }

            return humoCardInfoList(new HumoCardInfoRequest(pan), loanExtId, card.getCardId());
        } catch (Exception error) {
            log.warn("event=humo.card-info.lookup.failed loanExtId={} cardId={} message={}",
                    loanExtId, card.getCardId(), error.getMessage());
            return null;
        }
    }

    private HumoCardInfoResponse.HumoCardResult humoCardInfoList(HumoCardInfoRequest request, String loanExtId, Long cardId) {
        var originalRequest = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                new HuMoCardV3InfoRequestParam(
                        request
                )
        );
        var startNanos = System.nanoTime();
        if (log.isDebugEnabled()) {
            log.debug("event=humo.card-info.middleware.request loanExtId={} cardId={} pan={}",
                    loanExtId, cardId, maskPan(request.cardNumber()));
        }

        HuMoCardBaseResponse<HumoCardInfoResponse.HumoCardResult> response = humoClient.getCustomerCardInfo(originalRequest);
        var durationMillis = (System.nanoTime() - startNanos) / 1_000_000;
        if (response == null) {
            log.warn("event=humo.card-info.middleware.empty_response loanExtId={} cardId={} durationMs={}",
                    loanExtId, cardId, durationMillis);
            return null;
        }

        log.debug("event=humo.card-info.middleware.completed loanExtId={} cardId={} hasResult={} durationMs={}",
                loanExtId, cardId, response.getResult() != null, durationMillis);
        return response.getResult();
    }
}
