package uz.nodir.notification.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.notification.mongo.entity.BrokerRecord;
import uz.nodir.notification.mongo.repository.BrokerRecordRepository;
import uz.nodir.notification.service.core.BrokerRecordDAO;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrokerRecordDAOImpl implements BrokerRecordDAO {
    private final BrokerRecordRepository repository;

    @Override
    public BrokerRecord save(BrokerRecord brokerRecord) {
        log.info("Save/Update broker record entity: {}", brokerRecord);
        return repository.save(brokerRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrokerRecord> findByUniqueField(String value) {
        log.info("Find records by value: {}", value);
        return repository.findAllByUniqueField(value);
    }
}
