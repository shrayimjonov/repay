package uz.nodir.notification.service.core;

import uz.nodir.notification.redis.entity.Fraud;

import java.util.List;

public interface FraudRedisDAO {
    Fraud createDebtorQueue(Fraud fraud);

    List<Fraud> findByPhone(String phone);

    List<Fraud> findByLoanExtId(String loanExtId);

    List<Fraud> findByLoanExtIdAndPhone(String loanExtId, String phone);

    List<Fraud> findAll();

    void deleteAll();
}
