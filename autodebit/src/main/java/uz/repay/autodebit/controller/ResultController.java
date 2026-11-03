package uz.repay.autodebit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.autodebit.model.result.request.LoanResultRequestDTO;
import uz.repay.autodebit.service.core.LoanResultService;
import uz.repay.autodebit.service.core.PayResultService;

@RestController
@RequestMapping("/api/v1/result")
@RequiredArgsConstructor
public class ResultController {
    private final PayResultService payResultService;
    private final LoanResultService loanResultService;
    @GetMapping("/bySession/{sessionId}")
    @Deprecated
    public ResponseEntity<?> getResultBySessionId(@PathVariable("sessionId") String sessionId) {
        return ResponseEntity.ok(payResultService.getResultsBySessionID(sessionId));
    }

    @GetMapping("/byLoan/{loanExt}")
    @Deprecated
    public ResponseEntity<?> getResultByLoanExt(@PathVariable("loanExt") String loanExt) {
        return ResponseEntity.ok(payResultService.getResultsByLoanExt(loanExt));
    }

    @PostMapping("/byLoan")
    public ResponseEntity<?> getResultsByLoan(@RequestBody @Valid LoanResultRequestDTO requestDTO) {
        return ResponseEntity.ok(
                loanResultService.getResultsByLoan(requestDTO)
        );
    }

}
