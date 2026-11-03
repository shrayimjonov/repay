package uz.repay.payment.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.client.SMSFastoFeignClient;
import uz.repay.payment.model.dto.fasto.sms.request.SMSDebitPayRequest;
import uz.repay.payment.model.dto.fasto.sms.response.SMSDebitPayResponse;
import uz.repay.payment.service.web.SMSFastoService;

@Service
@Slf4j
@RequiredArgsConstructor
public class SMSFastoServiceImpl implements SMSFastoService {
    private final SMSFastoFeignClient client;

    @Override
    public SimpleResponse<SMSDebitPayResponse> sendDebitMessage(SMSDebitPayRequest request) {
        log.info("Send debit pay sms with request: {}", request);
        try {
            var response = client.sendDebitMessage(request);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }
}
