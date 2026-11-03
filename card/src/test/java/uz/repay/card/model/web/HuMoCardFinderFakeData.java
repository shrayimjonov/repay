package uz.repay.card.model.web;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoCardsByPinflResult;
import uz.repay.card.model.dto.humo.response.HuMoCardBaseResponse;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class HuMoCardFinderFakeData {
    private final static String RESPONSE_ID = "test_humo_pinfl_2025";

    public static SimpleResponse<HuMoCardBaseResponse<HuMoCardsByPinflResult>> success() {
        var mb = new HumoCardInfoResponse.Mb(
                "on",
                "+998901234567",
                "Status of the customer mobile agreement is active"
        );

        var status = new HumoCardInfoResponse.Statuses(
                new ArrayList<>(
                        List.of(
                                new HumoCardInfoResponse.Item(
                                        "card",
                                        "000",
                                        "Approved",
                                        "2024-11-15T08:16:56.000+00:00"
                                )
                        )
                )
        );


        var data = new HuMoCardsByPinflResult.HuMoCardsByPinflData(
                PanFake.HUMO_FAKE_PAN,
                "2910",
                0L,
                status,
                mb
        );


        var clientResult = new HuMoCardsByPinflResult(
                List.of(data)
        );

        var huMoResult = new HuMoCardBaseResponse<>(
                RESPONSE_ID,
                clientResult,
                null
        );


        var response = new SimpleResponse<>(huMoResult);
        log.info("Build fake empty data: {}", response);
        return response;
    }

    public static SimpleResponse<HuMoCardBaseResponse<HuMoCardsByPinflResult>> successEmpty() {
        var clientResult = new HuMoCardsByPinflResult(
                List.of()
        );

        var huMoResult = new HuMoCardBaseResponse<>(
                RESPONSE_ID,
                clientResult,
                null
        );


        var response = new SimpleResponse<>(huMoResult);
        log.info("Build fake data: {}", response);
        return response;
    }

    public static SimpleResponse<HuMoCardBaseResponse<HuMoCardsByPinflResult>> failedFromPC() {
        log.info("Build fake error data");
        return new SimpleResponse<>(
                new HuMoCardBaseResponse<>(
                        RESPONSE_ID,
                        null,
                        new CoreError(
                                400,
                                "Cards not found!"
                        )
                )
        );
    }

    public static SimpleResponse<HuMoCardBaseResponse<HuMoCardsByPinflResult>> failedInternal() {
        log.info("Build fake error data");
        return new SimpleResponse<>(
                ResultState.InternalServerError.getCode(),
                "Something went wrong!"
        );
    }

}
