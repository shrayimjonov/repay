package uz.repay.autodebit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.autodebit.service.business.AutoPaymentService;

@RestController
@RequestMapping("/api/v1/control")
@RequiredArgsConstructor
public class AutoPayController {
    private final AutoPaymentService autoPaymentService;

    @PatchMapping("/off")
    public ResponseEntity<?> turnOffAutoPayment() {
        return ResponseEntity.ok(autoPaymentService.stop());
    }
}
