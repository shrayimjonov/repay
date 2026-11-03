package uz.repay.card.model.web;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.nciBilling.response.NCICardsByPersonCodeResponse;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class NCICardsByPersonCodeResponseFakeData {

    public static ResultData<List<NCICardsByPersonCodeResponse>> mockNCIBillingCarsByPersonCodeSuccessResponse(){
        var list = new ArrayList<NCICardsByPersonCodeResponse>();
        var firstCard = new NCICardsByPersonCodeResponse("00981", "60055515", PanFake.UZCARD_FAKE_PAN, "2712", "KIMDIR KIMDIROVICH");
       list.add(firstCard);
        log.info("return success result with data size: {}", list.size());
        return new ResultData<>(list);
    }

    public static ResultData<List<NCICardsByPersonCodeResponse>> nciBillingCarsByPersonCodeEmptySuccessResponse(){
        var list = new ArrayList<NCICardsByPersonCodeResponse>();
        log.info("return success empty result");
        return new ResultData<>(list);
    }


    public static ResultData<List<NCICardsByPersonCodeResponse>> mockNCIBillingCarsByPersonCodeFailedResponse(){
        var message = "Is unSuccess situation for mock";
        log.error(message);
        return new ResultData<>(581, message);
    }
}
