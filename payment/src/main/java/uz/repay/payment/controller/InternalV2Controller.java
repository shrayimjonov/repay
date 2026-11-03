package uz.repay.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.payment.service.business.InfoService;
import uz.repay.payment.service.business.PaymentServiceV2;

@RestController
@RequestMapping("/api/v2/internal")
@RequiredArgsConstructor
public class InternalV2Controller {
    private final PaymentServiceV2 paymentService;
    private final InfoService infoService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody @Valid PayRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.payV3(requestDTO,2));
    }

    @GetMapping("/course")
    public ResponseEntity<?> getCourse() {
        return ResponseEntity.ok(infoService.getCourse());
    }

    @PostMapping("/forcePay")
    public ResponseEntity<?> forcePay(@RequestBody @Valid PayRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.forcePayV3(requestDTO,2));
    }

}
