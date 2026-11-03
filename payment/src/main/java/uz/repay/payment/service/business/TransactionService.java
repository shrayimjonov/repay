package uz.repay.payment.service.business;

import lombok.NonNull;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.model.dto.core.transaction.request.TransactionByLoanRequestDTO;
import uz.repay.payment.model.dto.core.transaction.request.TransactionExcelRequestDTO;
import uz.repay.payment.model.dto.core.transaction.request.TransactionFilterRequestDTO;
import uz.repay.payment.model.dto.core.transaction.response.TransactionFilterResponseDTO;
import uz.repay.payment.model.dto.core.transaction.response.TransactionReportDTO;
import uz.repay.payment.model.dto.core.transaction.response.TransactionResponseDTO;
import uz.repay.payment.model.dto.core.transaction.response.TransactionsMonthlyResponseDTO;
import uz.repay.payment.model.dto.core.transaction.response.result.ExcelFile;

import java.util.List;

public interface TransactionService {
    ResultData<TransactionResponseDTO> getTransactionById(@NonNull Long id);

    ResultData<TransactionFilterResponseDTO> getWithFilter(@NonNull TransactionFilterRequestDTO requestDTO);

    ResultData<TransactionFilterResponseDTO> getByLoanId(@NonNull TransactionByLoanRequestDTO requestDTO);

    ResultData<List<TransactionsMonthlyResponseDTO>> getMonthlyTransactions();

    ResultData<List<TransactionReportDTO>> getReportByMonthly();

    ResultData<List<TransactionResponseDTO>> getHistoryByLoanId(@NonNull String loanExtId);

    ExcelFile generateExcel(@NonNull TransactionExcelRequestDTO requestDTO);

}
