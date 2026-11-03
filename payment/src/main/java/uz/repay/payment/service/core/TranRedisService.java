package uz.repay.payment.service.core;

import uz.repay.payment.redis.entity.Tran;

import java.util.Optional;
import java.util.UUID;

public interface TranRedisService {
    Tran save(Tran tran);

    Optional<Tran> getTranById(Long tranId);

    void deleteById(UUID id);
}
