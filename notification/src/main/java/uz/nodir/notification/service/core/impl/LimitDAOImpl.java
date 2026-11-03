package uz.nodir.notification.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.notification.model.enums.TemplateType;
import uz.nodir.notification.mongo.entity.Limit;
import uz.nodir.notification.mongo.repository.LimitRepository;
import uz.nodir.notification.service.core.LimitDAO;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class LimitDAOImpl implements LimitDAO {
    private final LimitRepository repository;


    @Override
    @Transactional
    public Limit save(Limit limit) {
        log.info("Save/Update limit entity: {}", limit);
        var result = repository.save(limit);

        log.info("Saved entity id: {}", result.getId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Limit> findByLoanAndPhoneAndType(String loanExtId, String phone, TemplateType type) {
        log.info("Find limit by: {} {} {}", loanExtId, phone, type);
        var result = repository.findByLoanExtIdAndPhoneAndTemplateType(loanExtId, phone, type);

        log.info("Result is: {}", result);
        return result;
    }


}
