package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.model.entity.Status;
import uz.repay.card.repository.StatusRepository;
import uz.repay.card.service.core.StatusDAO;
import uz.nodir.repayautoconfigure.utils.CacheUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusDAOImpl implements StatusDAO {
    private final StatusRepository repository;

    @Override
    @Transactional
    @CacheEvict(value = CacheUtils.TC_STATUS, allEntries = true)
    public Status save(Status status) {
        log.info("Save status entity: {}", status);
        return repository.save(status);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.TC_STATUS, sync = true)
    public List<Status> getAll() {
        log.info("Get all statuses List");
        return repository.findAll();
    }

    @Override
    @CacheEvict(value = CacheUtils.TC_STATUS, allEntries = true)
    @Transactional
    public void delete(Long id) {
        log.info("Delete Status with id: {}", id);
        repository.deleteById(id);
    }

    @Override
    @CacheEvict(value = CacheUtils.TC_STATUS, allEntries = true)
    public void cacheEvict() {
        log.info("Statuses cache evicting...");
    }


}
