package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.info.response.BankDayResponseDTO;
import uz.repay.loan.service.business.InfoService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/info", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "info-methods")
public class InfoController {
    private final InfoService infoService;

    @GetMapping("/bankDay")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_DAY_VIEW)")
    @Operation(summary = "Get bank day", description = "Check all branches bank day", operationId = "get-bank-day")
    public ResponseEntity<ResultData<List<BankDayResponseDTO>>> checkBankDay() {
        return ResponseEntity.ok(infoService.checkBankDay());
    }
}
