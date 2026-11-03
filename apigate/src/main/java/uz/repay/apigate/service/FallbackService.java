package uz.repay.apigate.service;

import org.springframework.web.server.ServerWebExchangeDecorator;
import uz.repay.common.model.dto.response.core.ResultData;

public interface FallbackService {
   ResultData<?> loggingRequest(String body, ServerWebExchangeDecorator serverWebExchangeDecorator);

   void loggingForFileService(String body, ServerWebExchangeDecorator serverWebExchangeDecorator);
}
