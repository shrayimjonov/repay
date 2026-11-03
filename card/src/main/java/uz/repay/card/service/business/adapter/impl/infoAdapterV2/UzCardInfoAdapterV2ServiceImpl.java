package uz.repay.card.service.business.adapter.impl.infoAdapterV2;

import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.adapter.CardBalanceFetchRequest;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;
import uz.repay.card.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.uzcard.response.result.P2PInfoResult;
import uz.repay.card.service.business.adapter.UzCardInfoAdapterV2Service;
import uz.repay.card.service.business.component.DisableCardsComponent;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.exception.TimeOutException;
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
public class UzCardInfoAdapterV2ServiceImpl implements UzCardInfoAdapterV2Service {
    private final UzCardService uzCardService;
    private final DisableCardsComponent disableCardsComponent;
    private final static String UZ_CARD_TYPE_CORPORATE = "corporate";

    @Override
    public SimpleResponse<List<LoanCardsResult>> getCardsBalance(List<CardBalanceFetchRequest> cardsList) {
        log.info("Fetch {} uzCards balance from SV-GATE", cardsList.size());

        if (cardsList.isEmpty()) {
            log.info("Request list size empty and will be exited from Cards Balance method!");
            return new SimpleResponse<>(List.of());
        }


        var tokens = new ArrayList<>(
                cardsList
                        .stream()
                        .filter(z -> {
                                    if (!z.getType().equals(CardType.token)) {
                                        log.error("UzCard card type must be token! Now: {}", z.getType());
                                        return false;
                                    }

                                    if (!z.getProcessing().equals(Processing.UZCARD)) {
                                        log.error("Processing must be UzCard! Now: {}", z.getProcessing());
                                        return false;
                                    }

                                    if (!z.getCard().matches(CardUtils.regexUzCardToken())) {
                                        log.error("UzCard: {} doesn't match to token regex!", z.getCard());
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
            log.info(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        var result = getCardsInfo(tokens);
        log.info("Uzcard balance result: {}",result);
        var responseList = new ArrayList<LoanCardsResult>();

        if (result.getError() != null) {
            String message = "SV-GATE UzCard fetching cards balance has error: %s".formatted(result.getError().getMessage());
            log.error(message);
            cardsList.forEach(c -> responseList.add(new LoanCardsResult(c.getBaseCard())));
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), result.getError().getMessage());
        }


        if (result.getData().getError() != null) {
            log.error("SV-GATE UzCard cards balance result has error: {}", result.getData().getError().getMessage());
            disableCardsComponent.ifTokensNotFoundSetNotActive(cardsList, result.getData().getError());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), result.getData().getError().getMessage());
        }


        disableCardsComponent.disableNotFoundCards(cardsList,
                result
                        .getData()
                        .getResult()
                        .stream()
                        .map(CardBaseResult::getId)
                        .toList());



        cardsList
                .forEach(
                        c -> result.getData().getResult()
                                .forEach(
                                        u -> {
                                            if (u.getId().equals(c.getCard())) {
                                                var preparedDTO = new LoanCardsResult(c.getCardId(), u, Processing.UZCARD);
                                                log.info("Prepared loan card result: {}", preparedDTO);
                                                responseList.add(preparedDTO);
                                            }
                                        }
                                )
                );

        log.info("UzCards fetch balance from SV-GATE finished");
        return new SimpleResponse<>(responseList);
    }

    private SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> getCardsInfo(ArrayList<String> tokenList) {
        try {
            var result = uzCardService.getCardsInfo(new BalanceRequest(tokenList));
            return new SimpleResponse<>(result);
        } catch (RetryableException r) {
            return new SimpleResponse<>(510, r.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(500, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<LoanCardsResult> getOneCardBalance(CardBalanceFetchRequest request) {
        String card = request.getCard();

        log.info("Get one SV-GATE UzCard card balance: {}", card);

        if (!request.getType().equals(CardType.token)) {
            var message = "UzCard card type must be token! Now: %s".formatted(request.getType());
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        if (!request.getProcessing().equals(Processing.UZCARD)) {
            var message = "Processing must be UzCard! Now: %s".formatted(request.getProcessing());
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        if (!card.matches(CardUtils.regexUzCardToken())) {
            var message = "UzCard: %s doesn't match to token regex!".formatted(card);
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        var result = getCardsInfo(
                new ArrayList<>(List.of(card))
        );

        if (!result.getStatus()) {
            String message = "UzCard fetching cards balance has error: %s".formatted(result.getError().getMessage());
            log.error(message);
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), result.getError().getMessage());
        }

        if (result.getData().getError() != null) {
            log.error("UzCard cards balance result has error: {}", result.getData().getError().getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), result.getData().getError().getMessage());
        }


        LoanCardsResult loanCardsResult = new LoanCardsResult(
                request.getCardId(),
                result.getData().getResult().getFirst(),
                Processing.UZCARD
        );

        log.info("Prepared one SV-GATE card result: {}", loanCardsResult);

        return new SimpleResponse<>(loanCardsResult);
    }


    @Override
    public SimpleResponse<CardShortInfoDTO> takeP2PInfo(String pan) {
        log.info("Take SV-GATE UzCard p2p info for: {}", pan);

        if (!pan.matches(CardUtils.regexUzCard())) {
            var message = "UzCard: %s doesn't match to pan regex!".formatted(pan);
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var response = getP2PInfoResult(pan);

        if (!response.getStatus()) {
            log.error("UzCard: {} p2pInfo fetching error: {}", pan, response.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), response.getError().getMessage());
        }

        if (response.getData().getError() != null) {
            log.error("UzCard: {} p2pInfo result is FAILED", pan);
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), response.getData().getError().getMessage());
        }

        var result = response.getData().getResult();

        if (result.getType().equals(UZ_CARD_TYPE_CORPORATE)) {
            var message = "UzCard: %s type is corporate. You can't add this type cards!".formatted(pan);
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        CardShortInfoDTO cardShortInfoDTO = new CardShortInfoDTO(
                result
        );

        log.info("Prepared SV-GATE p2pInfo result: {}", cardShortInfoDTO);
        return new SimpleResponse<>(cardShortInfoDTO);
    }

    private SimpleResponse<UzCardBaseResponse<P2PInfoResult>> getP2PInfoResult(String pan) {
        var request = new P2PInfoRequest(pan);
        try {
            var response = uzCardService.getP2PInfo(request);
            return new SimpleResponse<>(response);
        } catch (TimeOutException t) {
            log.error("{} fetch card result has error: {}", pan, t.getMessage());
            return new SimpleResponse<>(ResultState.TimeOutError.getMessage(), true);
        } catch (Exception e) {
            log.error("{} fetch card result has error: {}", pan, e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(Processing.UZCARD);
    }
}
