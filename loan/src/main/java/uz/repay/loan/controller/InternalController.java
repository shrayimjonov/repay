package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.loan.model.dto.core.loan.request.LoanReportRequestDTO;
import uz.repay.loan.service.business.DebtLoanService;
import uz.repay.loan.service.business.FeaturedLoansService;
import uz.repay.loan.service.business.LoanService;

@RestController
@RequestMapping("/api/v1/internal")
@Hidden
@RequiredArgsConstructor
public class InternalController {
    private final LoanService loanService;
    private final DebtLoanService debtLoanService;
    private final FeaturedLoansService featuredLoansService;

    @PostMapping("/createLoan")
    public ResponseEntity<?> createLoan(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.createLoan(requestDTO));
    }

    @GetMapping("/debtLoans")
    public ResponseEntity<?> getAllDebtLoans() {
        return ResponseEntity.ok(debtLoanService.getAll());
    }

    @PostMapping("/balance")
    public ResponseEntity<?> getLoanBalance(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.getLoanBalanceForInternal(requestDTO));
    }

    @PostMapping("/getLoans")
    public ResponseEntity<?> getAllLoans(@RequestBody @Valid LoanReportRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.findAll(requestDTO));
    }

    @DeleteMapping("/debtLoan")
    public ResponseEntity<?> deleteById(@RequestParam("id") Long id) {
        return ResponseEntity.ok(debtLoanService.deleteById(id));
    }

    @GetMapping("/featuredLoanByUser")
    public ResponseEntity<?> getUserFeaturedLoans(@RequestParam("username") String username) {
        return ResponseEntity.ok(featuredLoansService.findUserFeaturedLoansForInternal(username));
    }

    @GetMapping("/takeLoanFeaturedCreators")
    public ResponseEntity<?> takeLoanFeaturedCreatorsList(@RequestParam("loanExtId") String loanExtId) {
        return ResponseEntity.ok(loanService.takeLoanFeaturedCreators(loanExtId));
    }

    @PostMapping("/balance/v3")
    public ResponseEntity<?> getLoanBalanceV3(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.getLoanBalanceInfoV3(requestDTO));
    }

    @PostMapping("/additional")
    public ResponseEntity<?> getLoanAdditionalInfo(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.takeLoanAdditionalInfo(requestDTO));
    }

    @GetMapping("/insolvents")
    public ResponseEntity<?> getIsInsolventLoans() {
        return ResponseEntity.ok(loanService.takeOnlyInsolventLoans());
    }

    @DeleteMapping("/debtLoanByLoanExtId")
    public ResponseEntity<?> deleteByLoanExtId(@RequestParam("id") String id) {
        return ResponseEntity.ok(debtLoanService.deleteByLoanExtId(id));
    }

}
