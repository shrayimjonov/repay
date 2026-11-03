package uz.repay.card.model.web;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class AutoPayResponseFakeData {
    public static final String RESPONSE_ID = "test_autopay_response_01";

    public static ResultData<UzCardBaseResponse<List<SVGateAutoPayCardsResult>>> buildSuccessResponse() {
        var uzCardResponse = buildAutoPaySuccessList();

        var result = ResultData.ok(uzCardResponse);

        log.info("Build fake success result: {}", result);
        return result;
    }

    public static UzCardBaseResponse<List<SVGateAutoPayCardsResult>> buildAutoPaySuccessList() {
        var autoPayCard1 = new SVGateAutoPayCardsResult(
                PanFake.AUTOPAY_TOKEN_1,
                "626273******1117",
                "TESTOV TEST",
                "998901234567"
        );

        var autoPayCard2 = new SVGateAutoPayCardsResult(
                PanFake.AUTOPAY_TOKEN_2,
                "998901234567",
                "SAMPLEV SAMPLE",
                "998901234567"
        );

        var list = List.of(autoPayCard1, autoPayCard2);

        return new UzCardBaseResponse<>(
                "2.0",
                RESPONSE_ID,
                list,
                null
        );
    }

    public static ResultData<UzCardBaseResponse<List<SVGateAutoPayCardsResult>>> buildFailedResponse() {
        var error = new CoreError(
                -1,
                "Cards not found!"
        );


        log.info("Build fake error result: {}", error);
        return new ResultData<>(
                new UzCardBaseResponse<>(
                        "2.0",
                        RESPONSE_ID,
                        null,
                        error
                )
        );
    }

    public static ResultData<UzCardBaseResponse<List<SVGateAutoPayCardsResult>>> buildFeignFailedResponse() {
        var error = new CoreError(
                -2,
                "Something went wrong!"
        );


        log.info("Build fake error result: {}", error);
        return new ResultData<>(error, false);
    }


    public static ResultData<UzCardBaseResponse<List<SVGateAutoPayCardsResult>>> buildSuccessEmptyResponse() {
        log.info("Build fake empty success result");
        return  ResultData.ok(
                new UzCardBaseResponse<>(
                "2.0",
                RESPONSE_ID,
                List.of(),
                null
        ));
    }
}
