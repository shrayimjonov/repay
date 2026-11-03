package uz.repay.payment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.payment.model.dto.core.pay.reqeuest.ReverseRequestDTO;
import uz.repay.payment.service.business.PaymentService;
import uz.repay.payment.service.business.PaymentServiceV2;

@RestController
@RequestMapping("/api/v1/action")
@RequiredArgsConstructor
@Deprecated
public class PayController {
    private final PaymentService paymentService;
    private final PaymentServiceV2 paymentServiceV2;

    @PostMapping("/pay")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).PAY)")
    public ResponseEntity<?> pay(@RequestBody @Valid PayRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentServiceV2.payV3(requestDTO, 2));
    }

    @PostMapping("/reverse")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REVERSE_PAYMENT)")
    public ResponseEntity<?> reverse(@RequestBody @Valid ReverseRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.defaultReverseTransaction(requestDTO));
    }

    @PatchMapping("/sync")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).SYNC_TRANSACTION)")
    public ResponseEntity<?> synchronize(@RequestParam("tranId") @Min(1) Long tranId) {
        return ResponseEntity.ok(paymentService.synchronize(tranId));
    }

    @PostMapping("/reverseCredit")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REVERSE_ONLY_ABS)")
    public ResponseEntity<?> reverseCredit(@RequestBody @Valid ReverseRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.reverseCredit(requestDTO));
    }

    @PostMapping("/cancelDebit")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REVERSE_ONLY_CARD)")
    public ResponseEntity<?> cancelDebit(@RequestBody @Valid ReverseRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.cancelDebit(requestDTO));
    }


}
