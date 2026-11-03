package uz.repay.report.service.dao;

import uz.repay.report.model.entity.MostDebitUsers;
import uz.repay.report.model.entity.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionDAO {
    List<Transaction> getTransactionsByDate(LocalDate toDate);

    List<MostDebitUsers> getMostDebitUsers(int limit);
}
