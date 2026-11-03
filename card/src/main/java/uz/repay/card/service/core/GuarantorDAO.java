package uz.repay.card.service.core;


import uz.repay.card.model.entity.Guarantor;

import java.util.Optional;

public interface GuarantorDAO {
    Guarantor save(Guarantor guarantor);

    Optional<Guarantor> findById(Long id);
}
