package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.mongo.entity.PaidRequest;
import uz.repay.card.mongo.repository.PaidRequestRepository;
import uz.repay.card.service.core.PaidRequestMongoDAO;
import uz.repay.common.model.enums.Processing;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaidRequestMongoDAOImpl implements PaidRequestMongoDAO {
    private final PaidRequestRepository repository;

    @Override
    @Transactional
    public PaidRequest save(PaidRequest paidRequest) {
        log.info("Save/Update paid request entity: {}", paidRequest);
        return repository.save(paidRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaidRequest> findAllByProcessing(Processing processing) {
        log.info("find all paid requests by processing: {}", processing);
        return repository.findAllByProcessing(processing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaidRequest> findAllByLoanExtIdAndUsername(String loanExtId, String username) {
        log.info("find all paid requests by loanExtId: {} and username: {}", loanExtId, username);
        return repository.findAllByLoanExtIdAndUsername(loanExtId, username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaidRequest> findAllByLoanExtId(String loanExtId) {
        log.info("Find all paid requests by loanExtId: {}", loanExtId);
        return repository.findAllByLoanExtId(loanExtId);
    }
}
