package uz.repay.loan.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.loan.model.entity.Guarantor;
import uz.repay.loan.repository.GuarantorRepository;
import uz.repay.loan.service.core.GuarantorDAO;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class GuarantorDAOImpl implements GuarantorDAO {
    private final GuarantorRepository repository;

    @Override
    @Transactional
    public Guarantor save(Guarantor guarantor) {
        log.info("Save/Update guarantor entity: {}", guarantor);
        return repository.save(guarantor);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Guarantor> findByPersonCode(String personCode) {
        log.info("Find guarantor by person code: {}", personCode);
        var result = repository.findByPersonCode(personCode);

        log.info("Find guarantor result is: {}", result);
        return result;
    }
}
