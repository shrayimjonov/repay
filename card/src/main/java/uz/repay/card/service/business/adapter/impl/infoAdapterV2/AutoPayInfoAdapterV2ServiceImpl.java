package uz.repay.card.service.business.adapter.impl.infoAdapterV2;

import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.adapter.CardBalanceFetchRequest;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.BalanceResult;
import uz.repay.card.service.business.adapter.AutoPayInfoAdapterV2Service;
import uz.repay.card.service.business.component.DisableCardsComponent;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoPayInfoAdapterV2ServiceImpl implements AutoPayInfoAdapterV2Service {
    private final UzCardService uzCardService;
    private final DisableCardsComponent disableCardsComponent;
    private final CardDAO cardDAO;

    @Override
    public SimpleResponse<List<LoanCardsResult>> getCardsBalance(List<CardBalanceFetchRequest> cardsList) {
        log.info("Fetch {} uzCards balance from AUTO-PAY", cardsList.size());

        if (cardsList.isEmpty()) {
            log.info("Request list size empty and will be exited from Cards Balance method!");
            return new SimpleResponse<>(List.of());
        }

        var tokens = new ArrayList<>(
                cardsList
                        .stream()
                        .filter(z -> {
                                    if (!z.getType().equals(CardType.token)) {
                                        log.error("AUTO-PAY balance request rejected: reason=invalid_card_type, expected=token, actual={}, request={}",
                                                z.getType(), summarizeRequest(z));
                                        return false;
                                    }

                                    if (!z.getProcessing().equals(Processing.SVGATE_AUTOPAY)) {
                                        log.error("AUTO-PAY balance request rejected: reason=invalid_processing, expected={}, actual={}, request={}",
                                                Processing.SVGATE_AUTOPAY, z.getProcessing(), summarizeRequest(z));
                                        return false;
                                    }

                                    if (!z.getCard().matches(CardUtils.regexUzCardToken())) {
                                        log.error("AUTO-PAY balance request rejected: reason=token_regex_mismatch, request={}", summarizeRequest(z));
                                        return false;
                                    }

                                    return true;
                                }
                        )
                        .map(CardBalanceFetchRequest::getCard)
                        .toList()
        );

        if (tokens.isEmpty()) {
            String message = "After handled, tokens list is empty and will be exited from Cards Balance method!";
            log.info("{} Requests: {}", message, summarizeRequests(cardsList));
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        var result = getBalanceInfo(tokens);

        if (result.getError() != null) {
            String message = "AUTO-PAY UzCard fetching cards balance has error: %s".formatted(result.getError().getMessage());
            log.error("{} Requests: {}", message, summarizeRequests(cardsList));
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), result.getError().getMessage());
        }

        var responseList = new ArrayList<LoanCardsResult>();

        if (result.getData() == null) {
            String message = "AUTO-PAY UzCard fetching cards balance returned null data";
            log.error("{} Requests: {}", message, summarizeRequests(cardsList));
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), message);
        }

        if (result.getData().getError() != null) {
            log.error("AUTO-PAY UzCard cards balance result has error: {}", result.getData().getError().getMessage());
            disableCardsComponent.ifTokensNotFoundSetNotActive(cardsList, result.getData().getError());
            cardsList.forEach(c -> responseList.add(new LoanCardsResult(c.getBaseCard())));
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), result.getData().getError().getMessage());
        }

        if (result.getData().getResult() == null || result.getData().getResult().getBalanceInfo() == null) {
            String message = "AUTO-PAY UzCard cards balance result or balanceInfo is null";
            log.error("{} Requests: {}", message, summarizeRequests(cardsList));
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), message);
        }

        var returnedTokenIds = result
                .getData()
                .getResult()
                .getBalanceInfo()
                .stream()
                .map(BalanceResult.BalanceInfo::getId)
                .toList();

        disableCardsComponent.disableNotFoundCards(cardsList,
                returnedTokenIds
        );

        var missingRequests = cardsList
                .stream()
                .filter(request -> returnedTokenIds.stream().noneMatch(request.getCard()::equals))
                .toList();
        if (!missingRequests.isEmpty())
            log.warn("AUTO-PAY balance response does not contain requested cards: missingCount={}, missingCards={}, responseIds={}",
                    missingRequests.size(), summarizeRequests(missingRequests), summarizeValues(returnedTokenIds));


        cardsList
                .forEach(
                        z -> result.getData().getResult()
                                .getBalanceInfo()
                                .forEach(
                                        u -> {
                                            log.info("AUTO-PAY balance response contains card z-object: {}", summarizeRequest(z));
                                            log.info("AUTO-PAY balance response contains card u-object : {}", u);
                                            if (u.getId().equals(z.getCard())) {
                                                var card = cardDAO.findById(z.getCardId());
                                                var preparedDTO = new LoanCardsResult(z.getCardId(), u, card, Processing.SVGATE_AUTOPAY);
                                                responseList.add(preparedDTO);
                                            }
                                        }
                                )
                );

        return new SimpleResponse<>(responseList);
    }

    private SimpleResponse<UzCardBaseResponse<BalanceResult>> getBalanceInfo(ArrayList<String> tokenList) {
        try {
            var result = uzCardService.getCardsBalance(new BalanceRequest(tokenList));
            return new SimpleResponse<>(result);
        } catch (RetryableException r) {
            return new SimpleResponse<>(510, r.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(500, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<LoanCardsResult> getOneCardBalance(CardBalanceFetchRequest request) {
        log.error("Method get One Card Balance not necessary for processing: {}", Processing.SVGATE_AUTOPAY);
        return new SimpleResponse<>(501, ResultState.NOT_IMPLEMENTED.getMessage());
    }

    @Override
    public SimpleResponse<CardShortInfoDTO> takeP2PInfo(String pan) {
        log.error("Method take P2P Info not necessary for processing: {}", Processing.SVGATE_AUTOPAY);
        return new SimpleResponse<>(501, ResultState.NOT_IMPLEMENTED.getMessage());
    }

    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.SVGATE_AUTOPAY);
    }

    private static String summarizeRequests(List<CardBalanceFetchRequest> requests) {
        if (requests == null || requests.isEmpty())
            return "[]";

        return requests
                .stream()
                .limit(10)
                .map(AutoPayInfoAdapterV2ServiceImpl::summarizeRequest)
                .toList()
                .toString();
    }

    private static String summarizeRequest(CardBalanceFetchRequest request) {
        if (request == null)
            return "null";

        var card = request.getBaseCard() == null ? null : request.getBaseCard().getCard();
        return "{ maskedPan=%s, cardId=%s, processing=%s, type=%s, token=%s, payer=%s}"
                .formatted(
                        card == null ? null : card.getMaskedPan(),
                        request.getCardId(),
                        request.getProcessing(),
                        request.getType(),
                        request.getCard(),
                        request.getPayer()
                );
    }

    private static String summarizeValues(List<String> values) {
        if (values == null || values.isEmpty())
            return "[]";

        return values
                .stream()
                .toList()
                .toString();
    }
}
