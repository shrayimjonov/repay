package uz.repay.card.service.core;

import uz.repay.card.model.dto.core.request.switcher.SwitcherPatchDTO;
import uz.repay.card.redis.entity.Switcher;

import java.util.List;
import java.util.UUID;

public interface SwitcherRedisService {
    Switcher findByName(String switcher);

    List<Switcher> findAll();

    Switcher updateStatus(UUID id, Boolean newValue);
}
