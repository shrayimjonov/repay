package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.InpsInfo;
import uz.repay.loan.service.business.ClientIncomeService;

@RestController
@RequestMapping(value = "/api/v1/client", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "client-methods")
public class ClientController {
    private final ClientIncomeService clientIncomeService;

    @GetMapping("/income")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).GET_INCOME)")
    @Operation(summary = "Client Salary", description = "Get client last 12 months salary info", operationId = "client-salary-income")
    public ResponseEntity<ResultData<InpsInfo>> checkClientIncome(@RequestParam(value = "personCode") String personCode) {
        return ResponseEntity.ok(clientIncomeService.checkClientIncomeByPersonCode(personCode));
    }
}
