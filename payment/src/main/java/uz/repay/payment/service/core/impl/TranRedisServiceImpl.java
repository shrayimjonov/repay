package uz.repay.payment.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.payment.redis.entity.Tran;
import uz.repay.payment.redis.repository.TranRepository;
import uz.repay.payment.service.core.TranRedisService;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TranRedisServiceImpl implements TranRedisService {
    private final TranRepository repository;

    @Override
    public Tran save(Tran tran) {
        log.info("Save/Update tran redis entity: {}", tran);
        return repository.save(tran);
    }


    @Override
    public Optional<Tran> getTranById(Long tranId) {
        log.info("Get tran by id: {}", tranId);
        return repository.findByTransactionId(tranId);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Delete by tranId: {}", id);
        repository.deleteById(id);
    }
}
