package uz.repay.payment.service.business;

import lombok.NonNull;
import uz.repay.common.model.dto.request.backlash.BacklashCreateRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;

public interface BacklashInterService {
    ResultData<Boolean> createBacklash(@NonNull BacklashCreateRequestDTO requestDTO);
}
