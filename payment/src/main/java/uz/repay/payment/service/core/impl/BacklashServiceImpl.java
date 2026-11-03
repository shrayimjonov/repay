package uz.repay.payment.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.StatusType;
import uz.repay.payment.model.entity.Backlash;
import uz.repay.payment.repository.BacklashRepository;
import uz.repay.payment.service.core.BacklashService;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BacklashServiceImpl implements BacklashService {
    private final BacklashRepository repository;

    @Override
    @Transactional
    public void create(String loanExtId, Processing processing, Long spentTime, StatusType type) {
        var lastBacklashOptional = findLastNotUsedBacklash(type);

        if (lastBacklashOptional.isPresent()) {
            log.info("Backlash for PC: {} and for type: {} already has!", processing, type);
            return;
        }

        var preparedEntity = entityBuilder(loanExtId, processing, spentTime, type);

        repository.save(preparedEntity);
    }

    private static Backlash entityBuilder(String loanExtId, Processing processing, Long spentTime, StatusType type) {
        var preparedEntity = new Backlash(loanExtId, processing, spentTime, type);
        log.info("Creating backlash entity: {}", preparedEntity);
        return preparedEntity;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Backlash> findLastNotUsedBacklash(StatusType type) {
        log.info("Find last not used backlash...");
        return repository.findLastNotUsedBacklash(type.name());
    }

    @Override
    @Transactional
    public void update(Backlash backlash) {
        log.info("Update backlash: {}", backlash);
        repository.save(backlash);
    }
}
