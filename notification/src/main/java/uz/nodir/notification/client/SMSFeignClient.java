package uz.nodir.notification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.nodir.notification.client.configuration.SMSClientConfiguration;
import uz.nodir.notification.model.dto.sms.request.SMSSendRequest;
import uz.repay.common.model.dto.response.core.ResultData;


@FeignClient(value = "sms-gateway", url = "${external.sms.url}", configuration = SMSClientConfiguration.class)
public interface SMSFeignClient {

    @PostMapping("/api/v2/send")
    ResultData<String> send(SMSSendRequest request);

}
