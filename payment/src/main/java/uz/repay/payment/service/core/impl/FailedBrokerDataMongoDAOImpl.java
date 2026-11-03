package uz.repay.payment.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.payment.mongo.entity.FailedBrokerData;
import uz.repay.payment.mongo.repository.FailedBrokerDataRepository;
import uz.repay.payment.service.core.FailedBrokerDataMongoDAO;


@Service
@Slf4j
@RequiredArgsConstructor
public class FailedBrokerDataMongoDAOImpl implements FailedBrokerDataMongoDAO {
    private final FailedBrokerDataRepository repository;

    @Override
    @Transactional
    public FailedBrokerData save(FailedBrokerData failedBrokerData) {
        log.info("Save/Update failedBrokerData entity: {}", failedBrokerData);
        var result = repository.save(failedBrokerData);

        log.info("Saved failedBrokerData entity id: {}", result.getId());
        return result;
    }
}
