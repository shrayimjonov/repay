package uz.nodir.notification.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.notification.model.entity.SMSProtocol;
import uz.nodir.notification.repository.SMSProtocolRepository;
import uz.nodir.notification.service.core.SMSProtocolDAO;

@Service
@Slf4j
@RequiredArgsConstructor
public class SMSProtocolDAOImpl implements SMSProtocolDAO {
    private final SMSProtocolRepository repository;

    @Override
    @Transactional
    public SMSProtocol save(SMSProtocol smsProtocol) {
        log.info("Save/Update sms protocol entity: {}", smsProtocol);
        return repository.save(smsProtocol);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SMSProtocol> findAll(Specification<SMSProtocol> specification, Pageable pageable) {
        log.info("Find all by filter params...");
        return repository.findAll(specification, pageable);
    }


}
