package uz.repay.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.InpsInfo;
import uz.repay.report.model.dto.report.request.ReportRequestDTO;
import uz.repay.report.service.business.ReportService;
import uz.repay.report.service.business.SalaryService;


@RestController
@RequestMapping(value = "/api/v1", consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "report-excel-methods")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final SalaryService salaryService;

    @PostMapping(value = "/universal", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponse(
            content = @Content(
                    schema = @Schema(type = "string", format = "binary"),
                    mediaType = "application/octet-stream"
            ),
            extensions = {
                    @Extension(properties = {@ExtensionProperty(name = "x-is-file", value = "true")})
            }
    )
    @Operation(summary = "Get Debtors report", description = "Download debtors excel report", operationId = "report-excel-debtors")
    public void getDebtors(@RequestBody @Valid ReportRequestDTO requestDTO) {
        reportService.generateExcelReport(requestDTO);
    }


    @PostMapping(value = "/salary", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponse(
            responseCode = "200",
            description = "Client income report stream",
            content = @Content(mediaType = "application/octet-stream")
    )
    @Operation(summary = "Client income report", description = "Download client incomes excel report", operationId = "report-excel-client-income")
    public void generateSalaryReport(@RequestBody @Valid ResultData<InpsInfo> requestDTO) {
        salaryService.generateSalaryReport(requestDTO);
    }

}
