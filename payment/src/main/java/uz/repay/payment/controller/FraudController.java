package uz.repay.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.model.dto.core.fraud.FraudClearListDTO;
import uz.repay.payment.service.core.FraudRedisService;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudController {
    private final FraudRedisService fraudRedisService;

    @GetMapping
    public ResponseEntity<?> getFraudsByLoan(@RequestParam("loanExtId") String loanExtId) {
        return ResponseEntity.ok(
                ResultData
                        .ok(fraudRedisService.findByLoanExtId(loanExtId))
        );
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN, T(uz.repay.common.model.enums.UserPermission).REPAY_FRAUD_CACHE_CLEAN)")
    public ResponseEntity<?> removeFraudsByLoan(@RequestParam("loanExtId") String loanExtId) {
        fraudRedisService.deleteByLoan(loanExtId);

        return ResponseEntity.ok(
                ResultData.ok(true)
        );
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN, T(uz.repay.common.model.enums.UserPermission).REPAY_FRAUD_CACHE_CLEAN)")
    public ResponseEntity<?> removeFraudsByLoanIds(@RequestBody FraudClearListDTO listDTO) {
        fraudRedisService.deleteByLoanExternalIds(listDTO);

        return ResponseEntity.ok(
                ResultData.ok(true)
        );
    }
}
