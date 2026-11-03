package uz.repay.card.model.web;

import lombok.extern.slf4j.Slf4j;
import uz.repay.card.model.data.PanFake;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.core.params.DebitCardParams;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Processing;


@Slf4j
public class ForcePayResponseFakeData {

    public static SimpleResponse<ResultData<PaymentForcePayResponseDTO>> success() {
        var paymentForcePayResponse = new PaymentForcePayResponseDTO();
        paymentForcePayResponse.setCurrency(Currency.UZS);
        paymentForcePayResponse.setLoanExtId("0098127386");
        paymentForcePayResponse.setRemainingAmount(0L);

        var debitParam = new DebitCardParams(
                PanFake.UZCARD_TOKEN_1,
                Processing.UZCARD
        );

        paymentForcePayResponse.setDebitCardParams(debitParam);


        var resultDataSimpleResponse = new SimpleResponse<>(
                ResultData.ok(
                        paymentForcePayResponse
                )
        );

        log.info("Build fake data: {}", resultDataSimpleResponse);
        return resultDataSimpleResponse;
    }
}
