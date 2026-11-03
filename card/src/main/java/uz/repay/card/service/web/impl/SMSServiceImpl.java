package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.SMSFastoFeignClient;
import uz.repay.card.client.SMSFeignClient;
import uz.repay.card.model.dto.sms.request.SMSRequest;
import uz.repay.card.model.dto.sms.response.BaseSmsResponse;
import uz.repay.card.model.dto.sms.response.SMSResponse;
import uz.repay.card.service.web.SMSService;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.exception.TimeOutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SMSServiceImpl implements SMSService {

    private final SMSFeignClient client;
    private final SMSFastoFeignClient smsFastoClient;

    @Override
    public SMSResponse send(SMSRequest request) {
        try {
            return client.send(request);
        } catch (RetryableException r) {
            throw new TimeOutException(r.getMessage());
        } catch (FeignException f) {
            throw new GatewayException(f.getMessage());
        } catch (Exception e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }


    @Override
    public BaseSmsResponse sendV2(SMSRequest request) {
        log.info("Send request to SMS Service: {}", request);
        try {
            var response = smsFastoClient.send(request);
            log.info("SMS response: {}", response);

            if (response.getCode() != 200)
                throw new InvalidDataException(response.getMessage());

            return response;
        } catch (RetryableException r) {
            throw new TimeOutException(r.getMessage());
        } catch (FeignException f) {
            throw new GatewayException(f.getMessage());
        } catch (Exception e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }


}
