package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.service.business.LoanModifyService;


@RestController
@RequestMapping(value = "/api/v1/modify", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "loan-modify-endpoint")
public class LoanModifyController {
    private final LoanModifyService loanModifyService;

    @PostMapping("/add")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).LOAN_ADD)")
    @Operation(summary = "Loan Add", description = "Create loan in database", operationId = "loan-modify-add")
    public ResponseEntity<ResultData<CoreError>> add(@RequestBody @Valid LoanSimpleRequestDTO requestDTO) {
        return ResponseEntity.ok(loanModifyService.add(requestDTO));
    }


}
