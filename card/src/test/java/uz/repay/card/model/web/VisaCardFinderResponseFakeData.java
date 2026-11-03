package uz.repay.card.model.web;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.elma.response.ElmaVisaCardFindResponse;
import uz.repay.card.model.dto.nciBilling.response.NCIClientInfoByPersonCode;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class VisaCardFinderResponseFakeData {

    public static SimpleResponse<List<ElmaVisaCardFindResponse.ElmaVisaCardResult>> success() {
        var list = new ArrayList<ElmaVisaCardFindResponse.ElmaVisaCardResult>();

        var cardResult = new ElmaVisaCardFindResponse.ElmaVisaCardResult(
                "00981",
                PanFake.VISA_FAKE_TOKEN_1,
                "22618840766729905900",
                "0",
                "Активна",
                "1127",
                "DAVR VISA CLASSIC"
        );

        list.add(cardResult);

        var result = new SimpleResponse<>(list.stream().toList());

        log.info("Build fake success data: {}", result);
        return result;
    }

    public static SimpleResponse<List<ElmaVisaCardFindResponse.ElmaVisaCardResult>> failed() {
        log.info("Build fake failed data");
        return new SimpleResponse<>(ResultState.NotProcessed.getCode(), "Something went wrong!");
    }

    public static SimpleResponse<List<ElmaVisaCardFindResponse.ElmaVisaCardResult>> successEmpty() {
        log.info("Build fake success empty data");
        return new SimpleResponse<>(List.of());
    }

    public static SimpleResponse<ElmaVisaCardFindResponse> failedFromPC() {
        var response = new ElmaVisaCardFindResponse();
        response.setRes(6);
        response.setErr("ORA-03113: end-of-file on communication channel\\nProcess ID: 3395944\\nSession ID: 12706 Serial number: 9733\\r\\n in ListCardsByCustomerList in RkoGetVISAClientsCards in lambda_method in <GetExecutor>b__9 in Execute; currMethod = ISBM.WebApi.Controllers.RkoController, method HandleErrorHelper");
        response.setCards(List.of());

        log.info("Build fake PC failed data: {}", response);
        return new SimpleResponse<>(response);
    }
}
