package uz.repay.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.model.dto.core.transaction.request.TransactionByLoanRequestDTO;
import uz.repay.payment.model.dto.core.transaction.request.TransactionExcelRequestDTO;
import uz.repay.payment.model.dto.core.transaction.request.TransactionFilterRequestDTO;
import uz.repay.payment.model.dto.core.transaction.response.TransactionResponseDTO;
import uz.repay.payment.service.business.TransactionService;


@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "API для управления транзакциями")
public class TransactionController {

    private static final String XLSX_MEDIA_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<ResultData<TransactionResponseDTO>> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping
    public ResponseEntity<?> getTransactionsByFilter(@RequestBody TransactionFilterRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.getWithFilter(requestDTO));
    }

    @Operation(
            summary = "Выгрузить транзакции в Excel"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл Excel во вложении",
                    content = @Content(mediaType = XLSX_MEDIA_TYPE)
            )
    })
    @PostMapping("/excel")
    public ResponseEntity<?> exportToExcel(@RequestBody TransactionExcelRequestDTO requestDTO) {
        var file = transactionService.generateExcel(requestDTO);

        var contentDisposition = ContentDisposition
                .attachment()
                .filename(file.getFileName())
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(XLSX_MEDIA_TYPE))
                .contentLength(file.getContent().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(file.getContent());
    }

    @PostMapping("/byLoan")
    public ResponseEntity<?> getTransactionsByFilter(@RequestBody TransactionByLoanRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.getByLoanId(requestDTO));
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyTransactions() {
        return ResponseEntity.ok(transactionService.getMonthlyTransactions());
    }

    @GetMapping("/report")
    public ResponseEntity<?> getTransactionByMonthly() {
        return ResponseEntity.ok(transactionService.getReportByMonthly());
    }

    @Operation(
            summary = "Получить историю транзакций по кредиту",
            description = "Возвращает полную историю всех транзакций для указанного кредита по его внешнему ID (ext_id), отсортированную по времени создания (от новых к старым). Публичный endpoint без авторизации."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно получена история транзакций",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResultData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный формат ext_id"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    @GetMapping("/history/{extId}")
    public ResponseEntity<?> getTransactionHistoryByLoanExtId(
            @Parameter(description = "Внешний ID кредита (ext_id из таблицы loan)", required = true, example = "00981170637")
            @PathVariable("extId") String extId) {
        return ResponseEntity.ok(transactionService.getHistoryByLoanId(extId));
    }

}
