package uz.repay.payment.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.common.model.enums.AddType;
import uz.repay.payment.model.entity.Temporary;
import uz.repay.payment.repository.TemporaryRepository;
import uz.repay.payment.service.core.TemporaryDAO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemporaryDAOImpl implements TemporaryDAO {
    private final TemporaryRepository repository;

    @Override
    @Transactional
    public Temporary save(Temporary temporary) {
        log.info("Save/Update temporary: {}", temporary);
        return repository.save(temporary);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Temporary> findById(Long id) {
        log.info("Find temporary by id: {}", id);
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Temporary> findByAddType(AddType type) {
        log.info("Find temporaries list by add type: {}", type);
        return repository.findByAddType(type);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting temporary by id: {}", id);
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Temporary> findAll() {
        log.info("Find All temporaries...");
        return repository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public List<Temporary> findByLoanExtId(String loanExtId) {
        log.info("Find temporary by loanExtId: {}", loanExtId);
        return repository.findByLoanExtId(loanExtId);
    }
}
