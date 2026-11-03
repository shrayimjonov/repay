package uz.repay.payment.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.repayautoconfigure.utils.CacheUtils;
import uz.repay.payment.model.entity.PCMaintenance;
import uz.repay.payment.repository.PCMaintenanceRepository;
import uz.repay.payment.service.core.PCMaintenanceDAO;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PCMaintenanceDAOImpl implements PCMaintenanceDAO {
    private final PCMaintenanceRepository repository;

    @Override
    @CacheEvict(value = CacheUtils.ABS_SETTINGS, allEntries = true)
    public PCMaintenance save(PCMaintenance pcMaintenance) {
        log.info("Save/Update ABS setting: {}", pcMaintenance);
        return repository.save(pcMaintenance);
    }

    @Override
    @Cacheable(value = CacheUtils.ABS_SETTINGS)
    @Transactional(readOnly = true)
    public List<PCMaintenance> getAll() {
        log.info("Get all ABS settings...");
        return repository.findAll();
    }

    @Override
    @CacheEvict(value = CacheUtils.ABS_SETTINGS, allEntries = true)
    public void deleteById(Long id) {
        log.info("Delete ABS setting by id: {}", id);
        repository.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheUtils.ABS_SETTINGS, allEntries = true)
    public void cacheEvict() {
        log.info("Cache {} all entries is evicted!", CacheUtils.ABS_SETTINGS);
    }

}
