package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.loan.response.LoanSimpleResponseDTO;
import uz.repay.loan.service.business.FeaturedLoansService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/fl", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "featured-loans-methods")
public class FeaturedLoansController {
    private final FeaturedLoansService featuredLoansService;

    @PatchMapping
    @Operation(summary = "Add new Featured loan", description = "Add loans to featured for each user", operationId = "create-featured-loan")
    public ResponseEntity<ResultData<CoreError>> create(@RequestParam("loanExtId") String loanExtId) {
        return ResponseEntity.ok(featuredLoansService.create(loanExtId));
    }

    @GetMapping
    @Operation(summary = "Find user featured loans", description = "Find all user's featured loans list", operationId = "find-featured-loans")
    public ResponseEntity<ResultData<List<LoanSimpleResponseDTO>>> findFeaturedLoans(){
        return ResponseEntity.ok(featuredLoansService.findUserFeaturedLoans());
    }
}
