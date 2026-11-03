package uz.repay.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.repay.common.exception.CardException;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardCommon {


    public Processing detectCardProcessing(String card) {
        if (card.matches(CardUtils.regexUzCard()))
            return Processing.UZCARD;
        else if (card.matches(CardUtils.regexHumo()))
            return Processing.HUMO;
        else if (card.matches(CardUtils.regexVisaDAVR()))
            return Processing.DAVR_VISA;
        else if (card.matches(CardUtils.regexMasterCardDAVR()))
            return Processing.DAVR_MASTER_CARD;
        else
            throw new CardException("Card processing type not detected from Pan Regex!");
    }

    public Boolean isDavrCard(String card) {
        if (card.matches(CardUtils.regexUzCard()))
            return card.matches(CardUtils.regexDavrUzCard());
        else if (card.matches(CardUtils.regexHumo()))
            return card.matches(CardUtils.regexDavrHumo());
        else if (card.matches(CardUtils.regexVisaDAVR()))
            return true;
        else if (card.matches(CardUtils.regexMasterCardDAVR()))
            return true;
        else
            throw new CardException("Card processing type not detected from Pan Regex!");
    }

    public static SimpleResponse<Processing> detectProcessingByToken(String card) {
        log.info("Detect processing for card: {}", card);

        if (card.matches(CardUtils.regexUzCardToken())) {
            var processing = Processing.UZCARD;
            log.info("Processing is: {}", processing);
            return new SimpleResponse<>(processing);
        } else if (card.matches(CardUtils.regexHuMoToken())) {
            var processing = Processing.HUMO;
            log.info("Processing is: {}", processing);
            return new SimpleResponse<>(processing);
        } else if (card.matches(CardUtils.regexVisaDAVRToken())) {
            var processing = Processing.DAVR_VISA;
            log.info("Processing is: {}", processing);
            return new SimpleResponse<>(processing);
        } else if (card.matches(CardUtils.regexDavrMasterCardToken())) {
            var processing = Processing.DAVR_MASTER_CARD;
            log.info("Processing is: {}", processing);
            return new SimpleResponse<>(processing);
        } else {
            var message = "Processing is not found for: %s".formatted(card);
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }
    }

    public static SimpleResponse<Processing> detectProcessingByPan(String card) {
        log.info("Detect processing for card: {}", card);

        if (card.matches(CardUtils.regexUzCard())) {
            var processing = Processing.UZCARD;
            log.info("Processing is: {}", processing);
            return new SimpleResponse<>(processing);
        } else if (card.matches(CardUtils.regexHumo())) {
            var processing = Processing.HUMO;
            log.info("Processing is: {}", processing);
            return new SimpleResponse<>(processing);
        } else if (card.matches(CardUtils.regexVisaDAVR())) {
            var processing = Processing.DAVR_VISA;
            log.info("Processing is: {}", processing);
            return new SimpleResponse<>(processing);
        } else if (card.matches(CardUtils.regexMasterCardDAVR())) {
            var processing = Processing.DAVR_MASTER_CARD;
            log.info("Processing is: {}", processing);
            return new SimpleResponse<>(processing);
        } else {
            var message = "Processing is not found for: %s".formatted(card);
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }
    }

}
