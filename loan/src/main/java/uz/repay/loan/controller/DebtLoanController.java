package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.debtLoan.request.DebtLoansRequestDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.DebtLoanDailyReportDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.DebtLoansResponseDTO;
import uz.repay.loan.service.business.DebtLoanService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/debtLoan", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "debtLoan-methods")
@RequiredArgsConstructor
public class DebtLoanController {
    private final DebtLoanService debtLoanService;

    @PostMapping("/filter")
    @Operation(summary = "DebtLoan Filter", description = "Find necessary debtLoans by theirs params", operationId = "get-all-debtors")
    public ResponseEntity<ResultData<DebtLoansResponseDTO>> getAll(@RequestBody DebtLoansRequestDTO requestDTO) {
        return ResponseEntity.ok(debtLoanService.findAll(requestDTO));
    }

    @PatchMapping("/cacheEvict")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    @Operation(summary = "DebtLoan Cache Evict", description = "Evict debt loan cache", operationId = "evict-debt-loan-cache")
    public ResponseEntity<ResultData<CoreError>> cacheEvict() {
        return ResponseEntity.ok(debtLoanService.evictCache());
    }

    @GetMapping("/dailyReport")
    @Operation(summary = "DebtLoan daily report", description = "Take daily debt loans total report", operationId = "debt-loan-daily-report")
    public ResponseEntity<ResultData<List<DebtLoanDailyReportDTO>>> dailyReport() {
        return ResponseEntity.ok(debtLoanService.findDailyReport());
    }


    @GetMapping("/reportByDate")
    @Operation(summary = "DebtLoan report by date", description = "Take debt loans report by date", operationId = "debt-loan-report-by-date")
    public ResponseEntity<ResultData<List<DebtLoanDailyReportDTO>>> reportByDate(
            @RequestParam("date") LocalDate date
    ) {
        return ResponseEntity.ok(debtLoanService.findDailyReportByDate(date));
    }

}
