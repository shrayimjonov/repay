package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.guarantor.request.GuarantorAddRequestDTO;
import uz.repay.loan.model.dto.core.guarantor.request.GuarantorToggleRequestDTO;
import uz.repay.loan.service.business.GuarantorService;

@RestController
@RequestMapping(value = "/api/v1/loanGuarantor", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "guarantor-endpoint")
public class GuarantorController {
    private final GuarantorService guarantorService;

    @PostMapping("/add")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_LOAN_ADD_GUARANTOR)")
    @Operation(summary = "New Guarantor", description = "Add guarantor to loan", operationId = "guarantor-add-new")
    public ResponseEntity<ResultData<CoreError>> addGuarantorToLoan(@RequestBody @Valid GuarantorAddRequestDTO requestDTO) {
        return ResponseEntity.ok(guarantorService.addGuarantorToLoan(requestDTO));
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_LOAN_TOGGLE_GUARANTOR)")
    @Operation(summary = "Toggle guarantor", description = "Change is deleted field to revert action", operationId = "guarantor-toggle")
    public ResponseEntity<ResultData<CoreError>> toggle(@RequestBody @Valid GuarantorToggleRequestDTO requestDTO) {
        return ResponseEntity.ok(guarantorService.deleteOrApproveGuarantor(requestDTO));
    }

}
