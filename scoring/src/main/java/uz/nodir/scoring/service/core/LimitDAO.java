package uz.nodir.scoring.service.core;

import uz.nodir.scoring.model.entity.Limit;

import java.util.List;
import java.util.Optional;

public interface LimitDAO {

    Limit save(Limit limit);

    List<Limit> getAll();

    void deleteById(Long id);

    void cacheEvict();

    Optional<Limit> findByType(String type);

    Optional<Limit> findById(Long id);
}
