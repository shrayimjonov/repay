package uz.nodir.scoring.service.web;

import uz.nodir.scoring.model.dto.huMoGateway.response.HuMoGateCardLimitResponse;
import uz.repay.common.model.dto.response.core.SimpleResponse;

public interface HuMoGateClientService {

    SimpleResponse<HuMoGateCardLimitResponse> getCardLimits(String pan);
}
