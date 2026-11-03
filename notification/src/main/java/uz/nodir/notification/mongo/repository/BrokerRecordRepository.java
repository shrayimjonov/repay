package uz.nodir.notification.mongo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.nodir.notification.mongo.entity.BrokerRecord;

import java.util.List;
import java.util.UUID;

@Repository
public interface BrokerRecordRepository extends CrudRepository<BrokerRecord, UUID> {

    List<BrokerRecord> findAllByUniqueField(String value);
}
