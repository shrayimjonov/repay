package uz.repay.apigate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchangeDecorator;
import uz.repay.apigate.service.FallbackService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;

@RestController
@RequiredArgsConstructor
public class FallbackController {
    private final FallbackService fallbackService;

    @RequestMapping("/fallback")
    public ResponseEntity<?> fallBack(@RequestBody(required = false) String body,
                                      ServerWebExchangeDecorator serverWebExchangeDecorator) {
        return
                ResponseEntity.ok(fallbackService.loggingRequest(body, serverWebExchangeDecorator));
    }


    @RequestMapping("/fileError")
    public ResponseEntity<?> fileServiceError(@RequestBody(required = false) String body,
                                              ServerWebExchangeDecorator serverWebExchangeDecorator) {
        fallbackService.loggingForFileService(body, serverWebExchangeDecorator);
        return ResponseEntity.ok(
                ResultData.error(
                        ResultState.InternalServerError,
                        "Something went wrong"
                )
        );
    }


}
