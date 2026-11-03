package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.loan.service.core.component.DebtLoanUpdaterComponent;

@RestController
@RequestMapping(value = "/api/v1/schedule", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "schedule-methods")
public class ScheduleController {
    private final DebtLoanUpdaterComponent updaterComponent;

    @PatchMapping
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REFRESH_DEBT_LOAN)")
    @Operation(summary = "Schedule refresh debt loan list", description = "Refresh debt loan list", operationId = "schedule-refresh-debtLoan")
    public ResponseEntity<ResultData<CoreError>> refreshDebtLoanList() {
        updaterComponent.refreshDebtLoanList();
        return ResponseEntity.ok(
                ResultData.ok(
                        new CoreError(0, ResponseStatus.SUCCESS.name())
                )
        );
    }

    @PatchMapping("/debt-loan-stats")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    @Operation(summary = "Schedule add debt loan stats", description = "Schedule debt loan stats", operationId = "schedule-add-debtLoan-stats")
    public ResponseEntity<ResultData<CoreError>> addDebtLoanStats() {
        updaterComponent.saveDailyDebtLoanReport();
        return ResponseEntity.ok(
                ResultData.ok(
                        new CoreError(0, ResponseStatus.SUCCESS.name())
                )
        );
    }
}
