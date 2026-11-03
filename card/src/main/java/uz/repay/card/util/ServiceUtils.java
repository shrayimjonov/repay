package uz.repay.card.util;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.dto.core.request.card.CardInfoParam;
import uz.repay.card.model.dto.core.request.card.CardPCCheckParam;
import uz.repay.card.model.dto.core.request.card.CardsParam;
import uz.repay.card.model.dto.core.response.card.CardCollectorDTO;
import uz.repay.card.model.entity.Card;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardCommon;
import uz.repay.common.utils.CardUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ServiceUtils {


    public static Boolean isExpiredCard(String expireDate) {
        if (expireDate.equals("1249"))
            return false;

        DateFormat dateformat = new SimpleDateFormat("yyMM");
        try {

            var expiryDate = dateformat.parse(expireDate);
            var date = LocalDate.ofInstant(
                            expiryDate.toInstant(), ZoneId.of("Asia/Tashkent")
                    )
                    .plusMonths(1);
            return LocalDate.now().isAfter(date);

        } catch (ParseException e) {
            throw new InvalidDataException("Can't parse card expire date: %s".formatted(e.getMessage()));
        }

    }

    public static String improvePhone(String phone) {
        if (phone.startsWith("+"))
            phone = phone.replaceAll("\\+", "");

        return phone;
    }

    /**
     * if add new pc configure this method for handling card balance
     *
     * @param card
     * @return
     */
    public static SimpleResponse<CardType> detectTypeByCard(String card) {
        if (card.matches(CardUtils.regexUzCardToken()) || card.matches(CardUtils.regexDavrMasterCardToken())
                || card.matches(CardUtils.regexHuMoToken()))
            return new SimpleResponse<>(CardType.token);
        else if (card.matches(CardUtils.regexMasterCardDAVR()) || card.matches(CardUtils.regexUzCard())
                || card.matches(CardUtils.regexHumo()) || card.matches(CardUtils.regexVisaDAVR()))
            return new SimpleResponse<>(CardType.pan);
        else
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), "Card type not detected for card: %s".formatted(card));
    }

    /**
     * if add new pc configure this method for handling card balance
     *
     * @param card
     * @return
     */
    public static SimpleResponse<String> takeDebitAccount(Card card) {
        switch (card.getProcessing()) {
            case UZCARD, SVGATE_AUTOPAY, DAVR_MASTER_CARD -> {
                return new SimpleResponse<>(card.getToken());
            }
            case HUMO, DAVR_VISA -> {
                return new SimpleResponse<>(CardUtils.decryptPan(card.getPan()));
            }
            default -> {
                return new SimpleResponse<>(ResultState.DataNotFound.getCode(), "Take debit account method can't detect debit account for card id: %d".formatted(card.getId()));
            }
        }
    }

    public static SimpleResponse<CardCollectorDTO> buildCardCollector(String card, CardPCCheckParam cardPCCheckParam) {
        var type = CardType.getCardType(card);


        var processingResult = type == CardType.pan ?
                CardCommon.detectProcessingByPan(card) : detectTokenMasterOrUzCard(card, cardPCCheckParam);

        if (!processingResult.getStatus())
            return new SimpleResponse<>(processingResult.getError().getCode(), processingResult.getError().getMessage());

        return new SimpleResponse<>(
                new CardCollectorDTO(
                        type,
                        processingResult.getData(),
                        card
                )
        );
    }

    public static List<CardsParam> buildCardParams(CardPCCheckParam cardPCCheckParam) {
        return cardPCCheckParam.getCards()
                .stream()
                .map(z -> ServiceUtils.buildCardCollector(z, cardPCCheckParam))
                .filter(SimpleResponse::getStatus)
                .map(SimpleResponse::getData)
                .collect(Collectors.groupingBy(
                        CardCollectorDTO::getProcessing,
                        Collectors.groupingBy(
                                CardCollectorDTO::getCardType,
                                Collectors.mapping(CardCollectorDTO::getCard, Collectors.toList())
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    Processing processing = entry.getKey();
                    var cardInfoParams = entry.getValue().entrySet().stream()
                            .map(cardTypeEntry -> new CardInfoParam(cardTypeEntry.getKey(), cardTypeEntry.getValue()))
                            .toList();
                    return new CardsParam(processing, cardInfoParams);
                })
                .toList();
    }

    public static SimpleResponse<Processing> detectTokenMasterOrUzCard(String card, CardPCCheckParam cardPCCheckParam) {
        var result = CardCommon.detectProcessingByToken(card);

        if (!result.getStatus())
            return new SimpleResponse<>(result.getError().getCode(), result.getError().getMessage());

        if (cardPCCheckParam.isMasterCard() && cardPCCheckParam.isAutoPayCard()) {
            var message = "AutoPay and Master card values are true at the same time!";
            log.error(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        if (cardPCCheckParam.isMasterCard()) {
            log.info("Processing is improved to: {}", Processing.DAVR_MASTER_CARD);
            return new SimpleResponse<>(Processing.DAVR_MASTER_CARD);
        }

        if (cardPCCheckParam.isAutoPayCard()) {
            log.info("Processing is improved to: {}", Processing.SVGATE_AUTOPAY);
            return new SimpleResponse<>(Processing.SVGATE_AUTOPAY);
        }

        return new SimpleResponse<>(result.getData());
    }
}
