package uz.nodir.scoring.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.scoring.model.entity.Limit;
import uz.nodir.scoring.repository.LimitRepository;
import uz.nodir.scoring.service.core.LimitDAO;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class LimitDAOImpl implements LimitDAO {
    private final LimitRepository repository;
    private final static String LIMIT_CACHE = "scoring_limit";

    @Override
    @Transactional
    @CacheEvict(value = LIMIT_CACHE, allEntries = true)
    public Limit save(Limit limit) {
        log.info("Save/Update limit entity: {}", limit);
        var entity = repository.save(limit);

        log.info("Saved limit entity id: {}", entity.getId());
        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = LIMIT_CACHE, sync = true)
    public List<Limit> getAll() {
        log.info("Get all limit entities");
        var entities = repository.findAll();

        log.info("Limit entities count: {}", entities.size());
        return entities;
    }

    @Override
    @Transactional
    @CacheEvict(value = LIMIT_CACHE, allEntries = true)
    public void deleteById(Long id) {
        log.info("Delete limit entity by id: {}", id);
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Limit> findByType(String type) {
        log.info("Find limit by type: {}", type);
        var result = repository.findByType(type);

        log.info("Find limit by type optional result is: {}", type);
        return result;
    }

    @Override
    public Optional<Limit> findById(Long id) {
        log.info("Find limit by id: {}", id);
        var result = repository.findById(id);

        log.info("Find limit by id optional result is: {}", id);
        return result;
    }


    @Override
    @CacheEvict(value = LIMIT_CACHE, allEntries = true)
    public void cacheEvict() {
        log.info("Cache evict limit entities");
    }
}
