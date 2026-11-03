package uz.repay.payment.service.core;

import uz.repay.common.model.enums.AddType;
import uz.repay.payment.model.entity.Temporary;

import java.util.List;
import java.util.Optional;

public interface TemporaryDAO {

    Temporary save(Temporary temporary);

    Optional<Temporary> findById(Long id);

    List<Temporary> findByAddType(AddType type);

    void deleteById(Long id);

    List<Temporary> findAll();

    List<Temporary> findByLoanExtId(String loanExtId);
}
