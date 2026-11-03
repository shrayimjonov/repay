package uz.repay.payment.service.core;

import uz.repay.common.model.enums.Processing;
import uz.repay.payment.redis.entity.Switcher;

import java.util.List;

public interface SwitcherRedisService {
    Switcher findByName(String switcher);
    List<Switcher> findAll();

    void switchProcessing(Processing processing, Boolean isEnable);
}
