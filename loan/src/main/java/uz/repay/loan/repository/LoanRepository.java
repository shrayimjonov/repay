package uz.repay.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.repay.common.model.enums.Currency;
import uz.repay.loan.model.entity.Loan;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    @Query("select l from Loan l left join fetch l.featuredLoans" +
            " left join fetch l.guarantors lg " +
            "left join fetch lg.guarantor where l.extId=:extId and l.isDeleted=:isDeleted")
    Optional<Loan> findByExtIdAndIsDeletedV2(String extId, Boolean isDeleted);

    Optional<Loan> findByExtIdAndIsDeleted(String extId, Boolean isDeleted);

    Optional<Loan> findByExtId(String extId);

    List<Loan> findAllByIsDeleted(Boolean isDeleted);

    List<Loan> findAllByIsInsolventAndIsDeleted(Boolean isInsolvent, Boolean isDeleted);

    List<Loan> findAllByCurrencyAndIsDeleted(Currency currency, Boolean isDeleted   );
}
