package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.response.annotations.LoanId;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.loan.response.LoanClientInfoResponseDTO;
import uz.repay.loan.service.business.LoanInfoService;


@RestController
@RequestMapping(value = "/api/v1/info", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Tag(name = "loan-info-endpoint")
public class LoanInfoController {
    private final LoanInfoService loanInfoService;

    @GetMapping("/client")
    @Operation(summary = "Loan client info", description = "Get loan client info", operationId = "loan-info-client")
    public ResponseEntity<ResultData<LoanClientInfoResponseDTO>> getLoanClientInfoByExtId(@RequestParam("extId") @LoanId(message = "Loan id must be consist of digits and more than 9 digits") String extId) {
        return ResponseEntity.ok(loanInfoService.getLoanClientInfoByExtId(extId));
    }

}
