package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.repayautoconfigure.utils.CacheUtils;
import uz.repay.card.model.entity.EPos;
import uz.repay.card.repository.EPosRepository;
import uz.repay.card.service.core.EPosDAO;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EPosDAOImpl implements EPosDAO {
    private final EPosRepository repository;

    @Override
    @CacheEvict(value = CacheUtils.TERMINAL, allEntries = true)
    public EPos save(EPos EPos) {
        log.info("Save/Update ePos: {}", EPos);
        return repository.save(EPos);
    }

    @Override
    @Cacheable(value = CacheUtils.TERMINAL)
    public List<EPos> getAll() {
        log.info("Get all ePosses from DB");
        try {
            return repository.findAll();
        } catch (Exception e) {
            log.error("EPOS_GET_LIST_ERROR: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    @CacheEvict(value = CacheUtils.TERMINAL, allEntries = true)
    public void deleteById(Long id) {
        log.info("Delete ePos By id: {}", id);
        repository.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheUtils.TERMINAL, allEntries = true)
    public void evictTerminalCache() {
        log.info("Cache {} all entries is evicted!", CacheUtils.TERMINAL);
    }


    @Override
    @Transactional
    @CacheEvict(value = CacheUtils.TERMINAL, allEntries = true)
    public void makeExceptional(List<String> terminalIds) {
        log.info("Make exceptional ePos with terminalIds: {}", terminalIds);
        repository.makeExceptional(terminalIds);
    }
}
