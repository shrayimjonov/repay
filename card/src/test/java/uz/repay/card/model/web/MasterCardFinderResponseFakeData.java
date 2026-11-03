package uz.repay.card.model.web;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.master.response.MasterCustomerCardsResponse;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class MasterCardFinderResponseFakeData {

    public static SimpleResponse<ResultData<ArrayList<MasterCustomerCardsResponse>>> success() {
        var customerResponse = new MasterCustomerCardsResponse(
                PanFake.MASTER_MASKED_PAN_1,
                PanFake.MASTER_TOKEN_1,
                "repay",
                "UNIVERSAL"
        );

        var resultData = ResultData.ok(
                new ArrayList<>(List.of(customerResponse)));

        var result = new SimpleResponse<>(resultData);

        log.info("Build fake success data: {}", result);
        return result;
    }

    public static SimpleResponse<ResultData<ArrayList<MasterCustomerCardsResponse>>> successEmpty() {
        log.info("Build fake success empty data");
        return new SimpleResponse<>(ResultData.ok(new ArrayList<>()));
    }

    public static SimpleResponse<ResultData<ArrayList<MasterCustomerCardsResponse>>> failedInternal() {
        log.info("Build fake internal error data");
        return new SimpleResponse<>(
                50001,
                "Something went wrong!"
        );
    }


    public static SimpleResponse<ResultData<ArrayList<MasterCustomerCardsResponse>>> failedFromPC() {
        log.info("Build fake pc error data");
        return new SimpleResponse<>(
                new ResultData<>(
                        50002,
                        "Client not found!"

                )
        );
    }
}
