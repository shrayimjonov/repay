package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.SMSFastoClientConfiguration;
import uz.repay.card.model.dto.sms.request.SMSRequest;
import uz.repay.card.model.dto.sms.response.BaseSmsResponse;


@FeignClient(value = "sms-fasto", url = "${external.fasto.sms.url}", configuration = SMSFastoClientConfiguration.class)
public interface SMSFastoFeignClient {
    @PostMapping("/sms/single")
    BaseSmsResponse send(SMSRequest request);

}
