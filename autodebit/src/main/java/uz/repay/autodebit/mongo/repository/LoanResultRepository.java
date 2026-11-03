package uz.repay.autodebit.mongo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.autodebit.mongo.entity.LoanResult;

import java.util.UUID;

@Repository
public interface LoanResultRepository extends CrudRepository<LoanResult, UUID> {
    Page<LoanResult> findByLoanExtId(String loanExtId, Pageable pageable);

}
