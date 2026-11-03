package uz.repay.card.model.service;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.core.request.card.CardPCCheckParam;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.card.util.ServiceUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.ArrayList;
import java.util.List;

import static uz.repay.card.model.entity.LoanEntity.loan;


@Slf4j
public class CardDetectorFakeData {


    public static SimpleResponse<CardSaverResultDTO<?>> getAbsCardDetectorResult() {
        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                false,
                                false,
                                List.of(
                                        PanFake.UZCARD_FAKE_PAN,
                                        PanFake.HUMO_FAKE_PAN
                                )
                        )
                ),
                false,
                null
        );
        log.info("Build abs detector result: {}", result);
        return new SimpleResponse<>(result);
    }

    public static SimpleResponse<CardSaverResultDTO<?>> getAutoPayCardDetectorResult() {
        List<SVGateAutoPayCardsResult> data = new ArrayList<>();
        data.add(new SVGateAutoPayCardsResult(
                PanFake.AUTOPAY_TOKEN_1,
                PanFake.AUTOPAY_PAN_1,
                "KIMDIR KIMDIROVICH",
                "998901234567"
        ));
        data.add(new SVGateAutoPayCardsResult(
                PanFake.AUTOPAY_TOKEN_2,
                PanFake.AUTOPAY_PAN_2,
                "TOSHMAT TOSHMATOV",
                "998901234567"
        ));

        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                false,
                                true,
                                data.stream().map(SVGateAutoPayCardsResult::getToken).toList()
                        )
                ),
                true,
                data
        );
        log.info("Build autoPay detector result: {}", result);
        return new SimpleResponse<>(result);
    }

    public static SimpleResponse<CardSaverResultDTO<?>> getDavrMobile1CardDetectorResult() {
        var tokens = new ArrayList<String>();
        tokens.add(PanFake.UZCARD_TOKEN_3);
        tokens.add(PanFake.HUMO_FAKE_TOKEN_2);


        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                false,
                                false,
                                tokens
                        )
                ),
                false,
                null
        );

        log.info("Build davr mobile 1.0 detector result: {}", result);
        return new SimpleResponse<>(result);
    }

    public static SimpleResponse<CardSaverResultDTO<?>> getDavrMobile2CardDetectorResult() {
        var tokenList = new ArrayList<String>();
        tokenList.add(PanFake.UZCARD_TOKEN_3);
        tokenList.add(PanFake.HUMO_FAKE_TOKEN_2);
        tokenList.add(PanFake.VISA_FAKE_TOKEN_1);


        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                false,
                                false,
                                tokenList
                        )
                ),
                false,
                null
        );

        log.info("Build davr mobile 2.0 detector result: {}", result);
        return new SimpleResponse<>(result);
    }

    public static SimpleResponse<CardSaverResultDTO<?>> getHuMoCardDetectorResult() {
        var huMoPanList = List.of(PanFake.HUMO_FAKE_PAN, PanFake.HUMO_FAKE_PAN_2);
        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                false,
                                false,
                                huMoPanList
                        )
                ),
                true,
                null
        );

        log.info("Build huMo card detector result: {}", result);
        return new SimpleResponse<>(result);
    }

    public static SimpleResponse<CardSaverResultDTO<?>> getMasterCardDetectorResult() {
        var masterTokens = List.of(PanFake.MASTER_TOKEN_1, PanFake.MASTER_TOKEN_2);

        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                true,
                                false,
                                masterTokens
                        )
                ),
                true,
                null
        );

        log.info("Build master card detector result: {}", result);
        return new SimpleResponse<>(result);
    }

    public static SimpleResponse<CardSaverResultDTO<?>> getVisaCardDetectorResult() {
        var visaTokens = List.of(PanFake.VISA_FAKE_TOKEN_1);

        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                false,
                                false,
                                visaTokens
                        )
                ),
                true,
                null
        );
        log.info("Build visa card detector result: {}", result);
        return new SimpleResponse<>(result);
    }

}
