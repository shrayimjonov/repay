package uz.repay.apigate.service.impl;

import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchangeDecorator;
import uz.repay.apigate.service.FallbackService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;

@Component
@Slf4j
public class FallBackServiceImpl implements FallbackService {

    @Override
    public ResultData<?> loggingRequest(String body, ServerWebExchangeDecorator serverWebExchangeDecorator) {
        log.info("Fallback request is: {}", body);
        log.info("Remote address: {}", serverWebExchangeDecorator.getRequest().getRemoteAddress());
        log.info("Requested URI: {}", serverWebExchangeDecorator.getDelegate().getRequest().getPath());
        log.error("Response is: {}", serverWebExchangeDecorator.getAttributes().getOrDefault("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.circuitBreakerExecutionException", null));

        return
                new ResultData<>(ResultState.InternalServerError, "%s is not responding or return 500 status"
                        .formatted(serverWebExchangeDecorator.getDelegate().getRequest().getPath()));
    }

    @Override
    public void loggingForFileService(String body, ServerWebExchangeDecorator serverWebExchangeDecorator) {
        log.info("Fallback request is: {}", body);
        log.info("Remote address: {}", serverWebExchangeDecorator.getRequest().getRemoteAddress());
        log.info("Requested URI: {}", serverWebExchangeDecorator.getDelegate().getRequest().getPath());
        log.error("Response is: {}", serverWebExchangeDecorator.getAttributes().getOrDefault("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.circuitBreakerExecutionException", null));

        throw new BadRequestException("File service throws exception");
    }

}
