package uz.repay.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.model.dto.core.pay.reqeuest.ReverseRequestDTO;
import uz.repay.payment.model.dto.core.pay.response.PayResponseDTO;
import uz.repay.payment.service.business.PaymentServiceV2;

@RestController
@RequestMapping(value = "/api/v2/action", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "pay_v2", description = "pay v2 endpoints")
public class PayV2Controller {
    private final PaymentServiceV2 paymentService;

    @PostMapping("/pay")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).PAY)")
    @Operation(summary = "pay", description = "debit from card to loan", operationId = "payment-action-pay")
    public ResponseEntity<ResultData<PayResponseDTO>> pay(@RequestBody @Valid PayRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.payV3(requestDTO,2));
    }

    @PostMapping("/reverse")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REVERSE_PAYMENT)")
    public ResponseEntity<?> reverse(@RequestBody @Valid ReverseRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.defaultReverseTransaction(requestDTO));
    }

    @PatchMapping("/sync")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).SYNC_TRANSACTION)")
    public ResponseEntity<?> synchronize(@RequestParam("tranId") @Min(1) @NotNull Long tranId) {
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


    @PatchMapping("/complete")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).COMPLETE_PAY)")
    public ResponseEntity<?> complete(@RequestParam("tranId") @Min(1) @NotNull Long tranId) {
        return ResponseEntity.ok(paymentService.completeTransaction(tranId));
    }

}
