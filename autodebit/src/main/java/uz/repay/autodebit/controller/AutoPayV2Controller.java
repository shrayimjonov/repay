package uz.repay.autodebit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.autodebit.model.core.request.AutoPaymentSwitcherDTO;
import uz.repay.autodebit.service.business.AutoPaymentServiceV2;
import uz.repay.common.model.dto.response.core.ResultData;

@RestController
@RequestMapping("/api/v2/control")
@RequiredArgsConstructor
public class AutoPayV2Controller {
    private final AutoPaymentServiceV2 autoPaymentService;

    @PatchMapping("/turn")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).AUTO_PAYMENT_ENABLE)")
    public ResponseEntity<?> turnOffAutoPayment(@RequestBody @Valid AutoPaymentSwitcherDTO dto) {
        autoPaymentService.switchAP(dto);
        return ResponseEntity.ok(
                new ResultData<>(dto.getEnable())
        );
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAPStatus() {
        return ResponseEntity.ok(
                autoPaymentService.getStatus()
        );
    }

    @GetMapping("/workers")
    public ResponseEntity<?> getAllWorkers() {
        return ResponseEntity.ok(
                autoPaymentService.getWorkers()
        );
    }

}
