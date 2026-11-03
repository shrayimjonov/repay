package uz.repay.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.payment.client.configuration.SMSFastoClientConfiguration;
import uz.repay.payment.model.dto.fasto.sms.request.SMSDebitPayRequest;
import uz.repay.payment.model.dto.fasto.sms.response.SMSDebitPayResponse;


@FeignClient(value = "sms-fasto", path = "/sms", url = "${external.fasto-sms.url}", configuration = SMSFastoClientConfiguration.class)
public interface SMSFastoFeignClient {
    @PostMapping("/api/v2/debitPay")
    SMSDebitPayResponse sendDebitMessage(SMSDebitPayRequest request);

}
