package uz.repay.card.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.card.CardFinderRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardToggleRequestDTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.service.business.LoanCardV2Service;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

@RestController
@RequestMapping("/api/v2/loanCard")
@RequiredArgsConstructor
@Tag(name = "loan-card-v2-endpoint")
public class LoanCardV2Controller {
    private final LoanCardV2Service service;

    @GetMapping
    @Operation(summary = "Get Loan Cards v2", description = "Get loan cards to fetch balance", operationId = "loan-cards-v2")
    public ResponseEntity<?> getLoanCards(@RequestParam("loanId") @Valid @Min(1) Long loanId) {
        return ResponseEntity.ok(service.getLoanAllCards(loanId));
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REMOVE_CARD)")
    @Operation(summary = "Toggle loan card", description = "Change card status to delete or approve", operationId = "loan-card-toggle")
    public ResponseEntity<ResultData<CoreError>> toggleCard(@RequestBody @Valid LoanCardToggleRequestDTO dto) {
        return ResponseEntity.ok(service.deleteOrApproveCard(dto));
    }

    @PostMapping("/findCards")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).FIND_NEW_CARDS)")
    @Operation(summary = "Find loan cards", description = "Find loan cards and add", operationId = "loan-card-finder")
    public ResponseEntity<ResultData<CardFinderResponseDTO>> findCards(@RequestBody @Valid CardFinderRequestDTO requestDTO) {
        return ResponseEntity.ok(service.findLoanCards(requestDTO));
    }
}
