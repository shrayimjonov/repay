package uz.repay.card.controller;

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
import uz.repay.card.model.dto.paidRequest.response.PaidRequestResponseDTO;
import uz.repay.card.service.business.PaidRequestService;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/pr", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "paid-requests-endpoint")
public class PaidRequestsController {
    private final PaidRequestService paidRequestService;

    @GetMapping("/byLoanExtId")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_PR_VIEW)")
    @Operation(summary = "find pr by loanExtId", description = "All paid request by loan", operationId = "pr-by-loan")
    public ResponseEntity<ResultData<List<PaidRequestResponseDTO>>> findByLoanExtId(@RequestParam String loanExtId) {
        return ResponseEntity.ok(paidRequestService.findRequestByLoanExtId(loanExtId));
    }
}
