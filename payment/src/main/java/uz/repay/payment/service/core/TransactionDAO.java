package uz.repay.payment.service.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import uz.repay.common.model.enums.Branch;
import uz.repay.payment.model.dto.core.transaction.request.TransactionFilterRequestDTO;
import uz.repay.payment.model.dto.core.transaction.response.result.TransactionExcelRow;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.entity.domain.MonthlyReportDTO;
import uz.repay.payment.model.entity.meta.TransactionMonthlyDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface TransactionDAO {
    Transaction save(Transaction transaction);

    Page<Transaction> findByFilter(Specification<Transaction> specification, Pageable pageable);

    Transaction findById(Long id);

    Optional<Transaction> findTranById(Long id);

    Page<Transaction> findByLoanId(Pageable pageable, String loanExtId);

    List<TransactionMonthlyDTO> findMonthlyTransactions();

    List<MonthlyReportDTO> getReportByMonthly();

    List<Transaction> getOrphanTransactions();

    List<Transaction> getActiveTransactions();

    List<Transaction> findHistoryByLoanId(String loanExtId);


    List<TransactionExcelRow> findRowsForExcel(TransactionFilterRequestDTO.TransactionParams params,
                                               List<Branch> branches,
                                               LocalDate afterCreatedDate,
                                               Long afterId,
                                               int limit);
}
