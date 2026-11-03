package uz.repay.report.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.report.model.entity.MostDebitUsers;
import uz.repay.report.service.business.analysis.AnalysisService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
@Tag(name = "report-analysis-endpoint")
public class AnalysisController {
    private final AnalysisService analysisService;

    @GetMapping("/most-debit-users")
    @Operation(description = "Get most debit users in this month", operationId = "report-analysis-mostDebitUsers")
    public ResponseEntity<ResultData<List<MostDebitUsers>>> getMostDebitUsers(@RequestParam(required = false, defaultValue = "5") int limit) {
        return ResponseEntity.ok(analysisService.mostDebitUsers(limit));
    }

}
