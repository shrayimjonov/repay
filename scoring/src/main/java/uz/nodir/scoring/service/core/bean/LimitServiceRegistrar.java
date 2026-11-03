package uz.nodir.scoring.service.core.bean;

import uz.nodir.scoring.service.business.limits.CardLimitAdapter;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;

public interface LimitServiceRegistrar {
    SimpleResponse<CardLimitAdapter> getCardLimitAdapterByPC(Processing pc);
}
