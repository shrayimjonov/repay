package uz.repay.autodebit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.autodebit.service.business.AutoPaymentServiceV2;
import uz.repay.autodebit.service.business.v4.AutoPaymentServiceV4;


@RestController
@RequestMapping("/api/v4/control")
@RequiredArgsConstructor
public class AutoPayV4Controller {
    private final AutoPaymentServiceV4 autoPaymentService;

    @PostMapping("/enable")
    public ResponseEntity<String> enableAutoPayment() {
        autoPaymentService.setActive(true);
        return ResponseEntity.ok("Auto payment is enabled");
    }

    @PostMapping("/disable")
    public ResponseEntity<String> disableAutoPayment() {
        autoPaymentService.setActive(false);
        return ResponseEntity.ok("Auto payment is disabled");
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> getAutoPaymentStatus() {
        return ResponseEntity.ok(autoPaymentService.isActive());
    }
}
