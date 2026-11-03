package uz.nodir.notification.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.notification.model.entity.Template;
import uz.nodir.notification.repository.TemplateRepository;
import uz.nodir.notification.service.core.TemplateDAO;
import uz.nodir.repayautoconfigure.utils.CacheUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateDAOImpl implements TemplateDAO {
    private final TemplateRepository repository;


    @Override
    @Transactional
    @CacheEvict(value = CacheUtils.SMS_TEMPLATE, allEntries = true)
    public Template save(Template template) {
        log.info("Save/Update template entity: {}", template);
        return repository.save(template);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.SMS_TEMPLATE, sync = true)
    public List<Template> findAll() {
        log.info("Find all templates...");
        return repository.findAll();
    }

    @Override
    @CacheEvict(value = CacheUtils.SMS_TEMPLATE, allEntries = true)
    public void deleteById(Long id) {
        log.info("Delete template by id: {}", id);
        repository.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheUtils.SMS_TEMPLATE, allEntries = true)
    public void evictTemplateCache() {
        log.info("Cache {} all entries is evicted!", CacheUtils.SMS_TEMPLATE);
    }

    @Override
    @Transactional(readOnly = true)
    @CacheEvict(cacheNames = CacheUtils.SMS_TEMPLATE, allEntries = true)
    public Page<Template> findAll(Specification<Template> specification, Pageable pageable) {
        log.info("Find templates by filter: {}", specification.toString());
        return repository.findAll(specification, pageable);
    }

}
