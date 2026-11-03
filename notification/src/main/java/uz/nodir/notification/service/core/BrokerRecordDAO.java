package uz.nodir.notification.service.core;

import uz.nodir.notification.mongo.entity.BrokerRecord;

import java.util.List;

public interface BrokerRecordDAO {
    BrokerRecord save(BrokerRecord brokerRecord);

    List<BrokerRecord> findByUniqueField(String value);
}
