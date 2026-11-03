package uz.repay.card.service.web;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import uz.repay.card.model.dto.sms.request.SMSRequest;
import uz.repay.card.model.dto.sms.response.BaseSmsResponse;
import uz.repay.card.model.dto.sms.response.SMSResponse;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.TimeOutException;

public interface SMSService {
    @Retryable(retryFor = {
            TimeOutException.class,
            GatewayException.class,
            GeneralErrorException.class
    }, maxAttempts = 4, backoff = @Backoff(delay = 1000))
    @Deprecated
    SMSResponse send(SMSRequest request);


    @Retryable(retryFor = {
            TimeOutException.class,
            GatewayException.class,
            GeneralErrorException.class
    }, maxAttempts = 1, backoff = @Backoff(delay = 3000))
    @Deprecated
    BaseSmsResponse sendV2(SMSRequest request);
}
