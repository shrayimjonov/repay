package uz.repay.card.model.web;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.dm.response.CardsByPINFLResponse;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DavrMobile1ResponseFakeData {

    public static ResultData<CardsByPINFLResponse> successResponse() {
        var response = new CardsByPINFLResponse();
        response.setTokens(
                new ArrayList<>(
                        List.of(
                                PanFake.HUMO_FAKE_TOKEN_1
                        )
                )
        );

        var result = ResultData.ok(response);

        log.info("Build fake success result: {}", result);
        return result;
    }

    public static ResultData<CardsByPINFLResponse> failedResponse() {
        var error = new CoreError(
                ResultState.InternalServerError.getCode(),
                "Something went wrong!"
        );


        log.info("Build fake error result: {}", error);
        return new ResultData<>(error, false);
    }

    public static ResultData<CardsByPINFLResponse> successEmptyResponse() {
        var response = new CardsByPINFLResponse();
        response.setTokens(new ArrayList<>());

        var result = ResultData.ok(response);

        log.info("Build fake empty success result: {}", result);
        return result;
    }
}
