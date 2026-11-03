package uz.repay.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.repay.loan.model.entity.Guarantor;

import java.util.Optional;


@Repository
public interface GuarantorRepository extends JpaRepository<Guarantor, Long> {

    Optional<Guarantor> findByPersonCode(String personCode);
}
