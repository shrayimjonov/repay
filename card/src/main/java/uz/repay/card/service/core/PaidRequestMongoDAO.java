package uz.repay.card.service.core;

import uz.repay.card.mongo.entity.PaidRequest;
import uz.repay.common.model.enums.Processing;

import java.util.List;

public interface PaidRequestMongoDAO {
    PaidRequest save(PaidRequest paidRequest);

    List<PaidRequest> findAllByProcessing(Processing processing);

    List<PaidRequest> findAllByLoanExtIdAndUsername(String loanExtId, String username);

    List<PaidRequest> findAllByLoanExtId(String loanExtId);
}
