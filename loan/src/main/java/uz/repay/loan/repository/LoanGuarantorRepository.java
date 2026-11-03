package uz.repay.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.repay.loan.model.entity.LoanGuarantor;
import uz.repay.loan.model.entity.pk.LoanGuarantorKey;

import java.util.Optional;


@Repository
public interface LoanGuarantorRepository extends JpaRepository<LoanGuarantor, LoanGuarantorKey> {

    @Query(value = "select lg from LoanGuarantor lg " +
            "left join fetch lg.loan " +
            "left join fetch lg.guarantor " +
            "where lg.loan.id = :loanId and lg.guarantor.id = :guarantorId")
    Optional<LoanGuarantor> findByLoanIdAndGuarantorId(Long loanId, Long guarantorId);
}
