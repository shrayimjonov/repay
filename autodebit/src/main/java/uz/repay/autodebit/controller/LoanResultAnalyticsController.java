package uz.repay.autodebit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.autodebit.model.analytics.LoanResultAnalyticsDTO;
import uz.repay.autodebit.model.analytics.PerMinuteBreakdownDTO;
import uz.repay.autodebit.service.core.LoanResultAnalyticsService;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/analytics/loan-results")
@RequiredArgsConstructor
public class LoanResultAnalyticsController {
    private final LoanResultAnalyticsService loanResultAnalyticsService;

    @GetMapping("/summary")
    public ResponseEntity<LoanResultAnalyticsDTO> getSummary(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from
    ) {
        return ResponseEntity.ok(loanResultAnalyticsService.getSummary(from));
    }

    @GetMapping("/summary/unique")
    public ResponseEntity<LoanResultAnalyticsDTO> getUniqueSummary(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from
    ) {
        return ResponseEntity.ok(loanResultAnalyticsService.getUniqueSummary(from));
    }

    @GetMapping("/per-minute")
    public ResponseEntity<List<PerMinuteBreakdownDTO>> getPerMinuteBreakdown(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from
    ) {
        return ResponseEntity.ok(loanResultAnalyticsService.getPerMinuteBreakdown(from));
    }
}
