package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.loan.model.dto.core.loan.request.LoanABSTranRequestDTO;
import uz.repay.loan.model.dto.core.loan.request.LoanChangeDataRequestDTO;
import uz.repay.loan.model.dto.core.loan.request.LoanRepayRequestDTO;
import uz.repay.loan.model.dto.core.loan.request.LoanSearchRequestDTO;
import uz.repay.loan.model.dto.core.loan.response.LoanABSTranResponseDTO;
import uz.repay.loan.model.dto.core.loan.response.LoanSearchResponseDTO;
import uz.repay.loan.model.dto.core.loan.response.LoanSimpleResponseDTO;
import uz.repay.loan.model.dto.nciBilling.response.ProblemResponse;
import uz.repay.loan.service.business.LoanService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "loan-methods")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).LOAN_ADD)")
    @Operation(summary = "Loan Create", description = "If loan exist in core banking create and returns externalId", operationId = "loan-create")
    @Deprecated
    public ResponseEntity<ResultData<LoanSimpleResponseDTO>> create(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.create(requestDTO));
    }

    @PostMapping("/filter")
    @Operation(summary = "Loans searching by params", description = "Find necessary loans by theirs params", operationId = "loan-filter-search")
    public ResponseEntity<ResultData<LoanSearchResponseDTO>> filter(@RequestBody LoanSearchRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.findLoansByFilter(requestDTO));
    }

    @PutMapping("/phone")
    @Operation(summary = "Loan Change Phone", description = "Find loan and update phone", operationId = "loan-update-phone")
    public ResponseEntity<ResultData<CoreError>> changePhone(@RequestBody @Valid LoanChangeDataRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.changeLoanData(requestDTO));
    }

    @PostMapping("/byLoan")
    @Operation(summary = "Loan Info", description = "Get loan info", operationId = "loan-findByExtId-info")
    @Deprecated
    public ResponseEntity<ResultData<LoanSimpleResponseDTO>> findById(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.findByExtId(requestDTO));
    }

    @PostMapping("/balance")
    @Operation(summary = "Loan Balance", description = "Get loan accounts balance", operationId = "loan-findByExtId-balance")
    public ResponseEntity<ResultData<LoanInfoResponse>> getLoanBalance(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.getLoanBalance(requestDTO));
    }

    @PatchMapping("/convict")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).CONVICT)")
    @Operation(summary = "Loan Convict", description = "Change loan status to isConvicted or reverse", operationId = "loan-convict")
    public ResponseEntity<ResultData<CoreError>> changeLoanConvict(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.convict(requestDTO));
    }

    @PostMapping("/fromABS")
    @Operation(summary = "Loan Transactions", description = "Get Loan Transactions from Core Banking", operationId = "loan-abs-transactions")
    public ResponseEntity<ResultData<List<LoanABSTranResponseDTO>>> getLoanTransFromABS(@RequestBody @Valid LoanABSTranRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.getLoanABSTransactions(requestDTO));
    }

    @DeleteMapping
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).LOAN_DELETE)")
    @Operation(summary = "Loan Delete", description = "Change loan status to isDeleted = true", operationId = "loan-delete-byExtId")
    public ResponseEntity<ResultData<CoreError>> deleteLoanByExt(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.deleteLoanByExt(requestDTO));
    }

    @PatchMapping("/cacheEvict")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    @Operation(summary = "Loan Cache Evict", description = "Evict Loan cache", operationId = "loan-cache-evict")
    public ResponseEntity<ResultData<CoreError>> cacheEvict() {
        return ResponseEntity.ok(loanService.evictCache());
    }


    @PostMapping("/repay")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).LOAN_REPAY)")
    @Operation(summary = "Loan repay immediately", description = "Immediately repayment loan by amount", operationId = "loan-repayment")
    public ResponseEntity<ResultData<ProblemResponse>> repay(@RequestBody @Valid LoanRepayRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.repay(requestDTO));
    }

    @PatchMapping("/autoDebitTurn")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).AUTO_DEBIT_SWITCH)")
    @Operation(summary = "Loan auto debit switcher", description = "Turn On/Off autoDebit process for loan", operationId = "loan-autoDebit-switcher")
    public ResponseEntity<?> changeAutoDebitTurnStatus(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.autoDebitTurn(requestDTO));
    }

    @PatchMapping("/insolvent")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_INSOLVENT)")
    @Operation(summary = "Loan insolvent", description = "Mark loan like insolvent debtor or remark", operationId = "loan-insolvent")
    public ResponseEntity<ResultData<CoreError>> changeLoanInsolvent(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanService.insolvent(requestDTO));
    }

}
