package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.SMSClientConfiguration;
import uz.repay.card.model.dto.sms.request.SMSRequest;
import uz.repay.card.model.dto.sms.response.SMSResponse;


@FeignClient(value = "sms-gateway", url = "${external.sms.integration-url}", configuration = SMSClientConfiguration.class)
public interface SMSFeignClient {

    @PostMapping("/message/send")
    SMSResponse send(SMSRequest request);

}
