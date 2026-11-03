package uz.repay.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.entity.domain.MonthlyReportDTO;
import uz.repay.payment.model.entity.meta.TransactionMonthlyDTO;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    Page<Transaction> findByLoanExtId(Pageable pageable, String loanExtId);

    @Query(value = "SELECT * FROM transaction WHERE loan_ext_id = :loanExtId ORDER BY init_time DESC", nativeQuery = true)
    List<Transaction> findByLoanExtIdOrderByInitTimeDesc(@Param("loanExtId") String loanExtId);

    @Query(value = "select created_date, count(*), sum(amount) as amount, branch,  pay_type  from \"transaction\" t \n" +
            "where status ='SUCCESS' and created_date >= date_trunc('month', CURRENT_DATE)\n" +
            "group by pay_type, created_date, branch\n" +
            "order by created_date desc", nativeQuery = true)
    List<TransactionMonthlyDTO> getMonthlyTransactionsByType();


    @Query(value = "select sum(amount) as amount, date_trunc('month',created_date) as date, count(*), pay_type  from \"transaction\" t \n" +
            "where status ='SUCCESS' AND created_date > current_date - INTERVAL '11 months' \n" +
            "group by date_trunc('month',created_date), pay_type \n" +
            "order by date desc", nativeQuery = true)
    List<MonthlyReportDTO> getReportByMonthly();

    @Query(value = "select t from Transaction t where t.status in (:statuses)")
    List<Transaction> getNeedConfirmTransactions(List<ResponseStatus> statuses);

    @Query(value = "select * from \"transaction\" g \n" +
            "where processing ='HUMO' and id in (\n" +
            "SELECT tran_id FROM bill b \n" +
            "WHERE tran_type  ='DEBIT' and status ='SUCCESS'\n" +
            "group by tran_id )\n" +
            "and id not in (\n" +
            "\n" +
            "\n" +
            "select tran_id  from bill b \n" +
            "where tran_type ='DEBIT_CONFIRM'\n" +
            "and tran_id in (\n" +
            "select id from \"transaction\" t \n" +
            "where processing ='HUMO' and id in (\n" +
            "SELECT tran_id FROM bill b \n" +
            "WHERE tran_type  ='DEBIT' and status ='SUCCESS'\n" +
            "group by tran_id )\n" +
            ")\n" +
            "and status ='SUCCESS' \n" +
            ")\n" +
            "and not exists (select 1 from bill c where g.id = c.tran_id and (tran_type = 'DEBIT_CANCEL' or tran_type='SYNCHRONIZE_PC') and status ='SUCCESS')\n" +
            "and status ='SUCCESS'\n" +
            "order by id desc;", nativeQuery = true)
    List<Transaction> getActiveTransactions();
}
