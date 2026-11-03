package uz.repay.payment.service.core;

import uz.repay.payment.model.dto.core.fraud.FraudClearListDTO;
import uz.repay.payment.redis.entity.Fraud;

import java.util.List;
import java.util.UUID;

public interface FraudRedisService {
    void save(Fraud fraud);

    List<Fraud> findByLoanExtId(String loanExtId);

    void deleteByLoan(String loanExtId);

    void deleteByLoanExternalIds(FraudClearListDTO listDTO);

    void deleteById(UUID id);

    List<Fraud> findByLoanExtIdAndPan(String loanExtId, String pan);
}
