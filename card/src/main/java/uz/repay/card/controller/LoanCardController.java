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
import uz.repay.card.model.dto.core.request.card.CardInfoRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardDeleteDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardsRequestDTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.core.response.card.CardInfoResponseDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.service.business.LoanCardService;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.LoanCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loanCard")
@RequiredArgsConstructor
@Tag(name = "loan-card-endpoint")
public class LoanCardController {
    private final LoanCardService service;

    @Deprecated
    @PostMapping
    @Operation(summary = "Get Loan Cards v1", description = "Get loan cards to fetch balance", operationId = "loan-cards-v1")
    public ResponseEntity<ResultData<LoanCardsResponseDTO<LoanCardsResult>>> getLoansCards(@RequestBody @Valid LoanCardsRequestDTO dto) {
        return ResponseEntity.ok(service.getLoanCards(dto));
    }

    @GetMapping
    @Operation(summary = "Get Loan Cards v1-2", description = "Get loan cards to fetch balance", operationId = "loan-cards-v1-2")
    public ResponseEntity<ResultData<List<LoanCardsResult>>> getLoanCards(@RequestParam("loanId") @Valid @Min(1) Long loanId) {
        return ResponseEntity.ok(service.getLoanAllCards(loanId));
    }

    @DeleteMapping
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REMOVE_CARD)")
    @Operation(summary = "Delete loan card", description = "Change card status to delete", operationId = "loan-card-delete")
    @Deprecated
    public ResponseEntity<ResultData<CoreError>> deleteLoanCard(@RequestBody @Valid LoanCardDeleteDTO dto) {
        return ResponseEntity.ok(service.deleteCardFromLoan(dto));
    }


    @PatchMapping
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    @Operation(summary = "Approve loan card", description = "Change card status to normal", operationId = "loan-card-approve")
    @Deprecated
    public ResponseEntity<ResultData<CoreError>> approveLoanCard(@RequestBody @Valid LoanCardDeleteDTO dto) {
        return ResponseEntity.ok(service.approveCardInLoan(dto));
    }


    @PostMapping("/info")
    @Operation(summary = "Get Card", description = "Get card info", operationId = "loan-card-info")
    public ResponseEntity<ResultData<CardInfoResponseDTO>> getCardInfo(@RequestBody @Valid CardInfoRequestDTO requestDTO) {
        return ResponseEntity.ok(service.getCardInfo(requestDTO));
    }

    @PostMapping("/findCards")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).FIND_NEW_CARDS)")
    @Operation(summary = "Find loan cards", description = "Find loan cards and add", operationId = "loan-card-finder")
    public ResponseEntity<ResultData<CardFinderResponseDTO>> findCards(@RequestBody @Valid CardFinderRequestDTO requestDTO) {
        return ResponseEntity.ok(service.findLoanCards(requestDTO));
    }

}
