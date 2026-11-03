package uz.repay.card.mongo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.card.mongo.entity.PaidRequest;
import uz.repay.common.model.enums.Processing;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaidRequestRepository extends CrudRepository<PaidRequest, UUID> {

    List<PaidRequest> findAllByProcessing(Processing processing);

    List<PaidRequest> findAllByLoanExtIdAndUsername(String loanExtId, String username);
    List<PaidRequest> findAllByLoanExtId(String loanExtId);
}
