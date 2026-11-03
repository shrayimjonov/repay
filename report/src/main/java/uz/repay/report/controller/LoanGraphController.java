package uz.repay.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.report.model.dto.report.request.LoanGraphRequestDTO;
import uz.repay.report.model.dto.report.response.LoanGraphResponseDTO;
import uz.repay.report.service.business.LoanGraphPDFService;

@RestController
@RequestMapping(value = "/api/v1/graph", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "graph-methods")
public class LoanGraphController {
    private final LoanGraphPDFService loanGraphPDFService;

    @PostMapping("/byLoan")
    @ApiResponse(
            responseCode = "200",
            description = "Loan graph report stream",
            content = @Content(mediaType = "application/octet-stream")
    )
    @Operation(summary = "Loan graph", description = "Download loan graph in PDF format", operationId = "graph-by-loan")
    public ResponseEntity<ResultData<LoanGraphResponseDTO>> getLoanGraph(@RequestBody @Valid LoanGraphRequestDTO requestDTO) {
        return ResponseEntity.ok(loanGraphPDFService.generateLoanGraphPDF(requestDTO));
    }

}
