package uz.repay.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.payment.model.dto.humo.request.ExecuteTransactionRequest;
import uz.repay.payment.service.business.PaymentServiceV2;
import uz.repay.payment.service.web.HuMoGatewayService;


@Slf4j
@RestController
@RequestMapping("/api/v3/internal")
@RequiredArgsConstructor
public class InternalV3Controller {
    private final PaymentServiceV2 paymentService;
    private final HuMoGatewayService huMoGatewayService;
    private final ObjectMapper objectMapper;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody @Valid PayRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.payV3(requestDTO, 2));
    }

    @PostMapping("/forcePay")
    public ResponseEntity<?> forcePay(@RequestBody @Valid PayRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.forcePayV3(requestDTO, 2));
    }

    @PostMapping("/executeTransaction")
    public ResponseEntity<?> executeTransaction(@RequestBody @Valid ExecuteTransactionRequest request) {
        log.info("InternalV3 executeTransaction request: {}", toJson(request));
        var response = huMoGatewayService.executeTransaction(request);
        log.info("InternalV3 executeTransaction response: {}", toJson(response));
        return ResponseEntity.ok(response);
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to serialize InternalV3 log object: {}", ex.getMessage());
            return String.valueOf(object);
        }
    }
}
