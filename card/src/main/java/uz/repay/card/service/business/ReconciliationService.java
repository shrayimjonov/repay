package uz.repay.card.service.business;

import jakarta.validation.constraints.NotNull;
import uz.repay.card.model.dto.core.request.reco.ReconciliationFilterRequestDTO;
import uz.repay.card.model.dto.core.response.reconciliation.ReconciliationFilterResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;


public interface ReconciliationService {

    ResultData<ReconciliationFilterResponseDTO> filter(@NotNull ReconciliationFilterRequestDTO requestDTO);
}
