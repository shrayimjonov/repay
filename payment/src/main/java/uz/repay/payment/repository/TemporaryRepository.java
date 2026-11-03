package uz.repay.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.repay.common.model.enums.AddType;
import uz.repay.payment.model.entity.Temporary;

import java.util.List;

@Repository
public interface TemporaryRepository extends JpaRepository<Temporary, Long> {
    List<Temporary> findByAddType(AddType addType);

    List<Temporary> findByLoanExtId(String loanExtId);
}
