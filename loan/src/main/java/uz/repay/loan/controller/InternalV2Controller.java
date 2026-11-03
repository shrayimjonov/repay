package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.debtLoan.response.DebtLoansPageResponseDTO;
import uz.repay.loan.service.business.DebtLoanService;
import uz.repay.loan.service.business.LoanModifyService;


@RestController
@RequestMapping("/api/v2/internal")
@RequiredArgsConstructor
@Hidden
@Validated
public class InternalV2Controller {
    private final LoanModifyService loanModifyService;
    private final DebtLoanService debtLoanService;

    @PostMapping("/loan")
    public ResponseEntity<ResultData<CoreError>> addLoan(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanModifyService.addWithoutBranchValidation(requestDTO));
    }


    @GetMapping("/debtLoans")
    public ResponseEntity<?> getAllDebtLoans() {
        return ResponseEntity.ok(debtLoanService.getAllV2());
    }

    @GetMapping("/debtLoans/page")
    public ResponseEntity<ResultData<DebtLoansPageResponseDTO>> getAllDebtLoansPage(
            @RequestParam("limit") @Min(value = 1) int limit,
            @RequestParam("pageNumber") @Min(value = 0) int pageNumber
    ) {
        return ResponseEntity.ok(debtLoanService.getAllV2Page(limit, pageNumber));
    }

}
