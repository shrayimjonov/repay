package uz.repay.payment.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.repay.payment.model.dto.core.fraud.FraudClearListDTO;
import uz.repay.payment.redis.entity.Fraud;
import uz.repay.payment.redis.repository.FraudRepository;
import uz.repay.payment.service.core.FraudRedisService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudRedisServiceImpl implements FraudRedisService {
    private final FraudRepository repository;

    @Override
    public void save(Fraud fraud) {
        log.info("Save new fraud: {}", fraud);
        repository.save(fraud);
    }

    @Override
    public List<Fraud> findByLoanExtId(String loanExtId) {
        log.info("Find fraud list by loanExtId: {}", loanExtId);
        return repository.findByLoanId(loanExtId);
    }

    @Override
    public void deleteByLoan(String loanExtId) {
        log.info("Delete all fraudulent records by loanExtId: {}", loanExtId);
        var loanList = findByLoanExtId(loanExtId);

        repository.deleteAll(loanList);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Delete fraud by id: {}", id);
        repository.deleteById(id);
    }


    @Override
    public List<Fraud> findByLoanExtIdAndPan(String loanExtId, String pan) {
        log.info("Find fraud list by loanExtId: {} and pan: {}", loanExtId, pan);
        return repository.findByLoanIdAndPan(loanExtId, pan);
    }

    @Override
    public void deleteByLoanExternalIds(FraudClearListDTO listDTO) {
        if (listDTO == null || CollectionUtils.isEmpty(listDTO.getLoanExternalIds())) {
            repository.deleteAll();
            return;
        }
        repository.deleteAllByLoanIdIn(listDTO.getLoanExternalIds());
    }
}
