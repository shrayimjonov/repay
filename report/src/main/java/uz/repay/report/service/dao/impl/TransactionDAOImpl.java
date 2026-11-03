package uz.repay.report.service.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.report.model.entity.MostDebitUsers;
import uz.repay.report.model.entity.Transaction;
import uz.repay.report.service.dao.TransactionDAO;

import java.time.LocalDate;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionDAOImpl implements TransactionDAO {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDate(LocalDate toDate) {
        String sql = "SELECT * FROM transactions WHERE createdAt > ?";
        return jdbcTemplate.query(sql, new Object[]{toDate}, (rs, rowNum) ->
                new Transaction(
                        rs.getLong("amount"),
                        rs.getString("phone"),
                        rs.getString("createdBy"),
                        rs.getString("fullName"),
                        rs.getString("processing"),
                        rs.getString("language"),
                        rs.getString("loanExtId"),
                        rs.getString("pan"),
                        rs.getString("payer"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MostDebitUsers> getMostDebitUsers(int limit) {
        log.info("Get most debit users by limit: {}", limit);
        var sql = "select createdBy, count(*) as count from transactions t \n" +
                "where createdBy <> 'external' and  createdAt >= date_trunc('month', NOW())\n" +
                "group by createdBy \n" +
                "order by count(*) desc\n" +
                "limit ?";

        try {
          return jdbcTemplate.query(sql, new Object[]{limit}, (rs, rowNum) ->
                    new MostDebitUsers(
                            rs.getString("createdBy"),
                            rs.getInt("count")
                    ));
        } catch (RuntimeException s) {
            log.error("Taken exception while executing get most Debit users method with message: {}", s.getMessage());
            throw new GeneralErrorException(s.getMessage());
        }
    }


}
