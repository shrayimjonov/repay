package uz.repay.payment.service.core;

import uz.repay.payment.mongo.entity.FailedBrokerData;

public interface FailedBrokerDataMongoDAO {
    FailedBrokerData save(FailedBrokerData failedBrokerData);
}
