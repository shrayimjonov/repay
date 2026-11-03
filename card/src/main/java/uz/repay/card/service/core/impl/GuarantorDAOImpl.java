package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.model.entity.Guarantor;
import uz.repay.card.repository.GuarantorRepository;
import uz.repay.card.service.core.GuarantorDAO;

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
        var result = repository.save(guarantor);

        log.info("Saved entity id: {}", guarantor.getId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Guarantor> findById(Long id) {
        log.info("Find guarantor by id: {}", id);
        var result = repository.findById(id);

        log.info("Find guarantor result is: {}", result);
        return result;
    }
}
