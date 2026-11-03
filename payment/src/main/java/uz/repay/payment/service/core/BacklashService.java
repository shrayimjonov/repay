package uz.repay.payment.service.core;

import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.StatusType;
import uz.repay.payment.model.entity.Backlash;

import java.util.Optional;

public interface BacklashService {
    void create(String loanExtId, Processing processing, Long spentTime, StatusType type);

    Optional<Backlash> findLastNotUsedBacklash(StatusType type);

    void update(Backlash backlash);

}
