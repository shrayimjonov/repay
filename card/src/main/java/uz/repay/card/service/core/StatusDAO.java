package uz.repay.card.service.core;

import uz.repay.card.model.entity.Status;

import java.util.List;

public interface StatusDAO {
    Status save(Status status);

    List<Status> getAll();

    void delete(Long id);

    void cacheEvict();
}
