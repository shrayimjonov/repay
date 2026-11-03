package uz.nodir.notification.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.notification.redis.entity.Fraud;
import uz.nodir.notification.redis.repository.FraudRepository;
import uz.nodir.notification.service.core.FraudRedisDAO;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudRedisDAOImpl implements FraudRedisDAO {
    private final FraudRepository repository;

    @Override
    @Transactional
    public Fraud createDebtorQueue(Fraud fraud) {
        log.info("Save/Update fraud entity: {}", fraud);
        return repository.save(fraud);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fraud> findByPhone(String phone) {
        log.info("Find fraud by phone: {}", phone);
        return repository.findByPhone(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fraud> findByLoanExtId(String loanExtId) {
        log.info("Find fraud by loanExtId: {}", loanExtId);
        return repository.findByLoanExtId(loanExtId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fraud> findByLoanExtIdAndPhone(String loanExtId, String phone) {
        log.info("Find fraud by: {} {}", loanExtId, phone);
        var result = repository.findByLoanExtIdAndPhone(loanExtId, phone);

        log.info("Found result is: {}", result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fraud> findAll() {
        log.info("Find all frauds list...");
        var result = repository.findAll();

        var list = new ArrayList<Fraud>();

        while (result.iterator().hasNext())
            list.add(result.iterator().next());


        log.info("Fraud result is: {} pcs", result);
        return list;
    }


    @Override
    @Transactional
    public void deleteAll() {
        log.info("Delete all frauds list...");
        repository.deleteAll();
    }
}
