package uz.repay.payment.mongo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.payment.mongo.entity.FailedBrokerData;

import java.util.UUID;

@Repository
public interface FailedBrokerDataRepository extends CrudRepository<FailedBrokerData, UUID> {
}
