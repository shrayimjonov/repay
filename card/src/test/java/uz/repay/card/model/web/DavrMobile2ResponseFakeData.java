package uz.repay.card.model.web;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.dm2.response.DM2CardsByPinflResponse;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

@Slf4j
public class DavrMobile2ResponseFakeData {

    public static ResultData<List<DM2CardsByPinflResponse>> successResponse() {
        var list = List.of(new DM2CardsByPinflResponse(PanFake.UZCARD_TOKEN_1));

        var result = ResultData.ok(list);

        log.info("Build fake success result: {}", result);
        return result;
    }

    public static ResultData<List<DM2CardsByPinflResponse>> failedResponse() {
        var error = new CoreError(
                ResultState.InternalServerError.getCode(),
                "Something went wrong!"
        );

        log.info("Build fake error result: {}", error);
        return new ResultData<>(error, false);
    }

    public static ResultData<List<DM2CardsByPinflResponse>> successEmptyResponse() {
        log.info("Build fake empty success result");
        return ResultData.ok(List.of());
    }
}
