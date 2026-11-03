package uz.repay.autodebit.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.repay.autodebit.mongo.entity.PayResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayResultRepository extends MongoRepository<PayResult, UUID> {
    Optional<PayResult> findBySessionId(String sessionId);

    List<PayResult> findAllByFailedListContains(String loanExt);
}
