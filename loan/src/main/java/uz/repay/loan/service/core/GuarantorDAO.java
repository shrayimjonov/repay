package uz.repay.loan.service.core;

import uz.repay.loan.model.entity.Guarantor;

import java.util.Optional;

public interface GuarantorDAO {
    Guarantor save(Guarantor guarantor);

    Optional<Guarantor> findByPersonCode(String personCode);
}
