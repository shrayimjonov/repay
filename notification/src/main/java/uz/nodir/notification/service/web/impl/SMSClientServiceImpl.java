package uz.nodir.notification.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.notification.client.SMSFeignClient;
import uz.nodir.notification.model.dto.sms.request.SMSSendRequest;
import uz.nodir.notification.service.web.SMSClientService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SMSClientServiceImpl implements SMSClientService {
    private final SMSFeignClient client;

    @Override
    public SimpleResponse<ResultData<String>> send(SMSSendRequest request) {
        log.info("From SMS Feign Client sending send message request: {}", request);

        try {
            var response = client.send(request);
            log.info("SMS Feign Client send message response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("SMS Feign Client send message method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("SMS Feign Client send message method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("SMS Feign Client send message method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }
}
