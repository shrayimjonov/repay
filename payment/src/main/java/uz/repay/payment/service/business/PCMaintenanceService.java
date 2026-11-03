package uz.repay.payment.service.business;

import jakarta.validation.constraints.NotNull;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.Processing;
import uz.repay.payment.model.dto.core.pcMaintenance.request.PCMaintenanceAddSettingsRequestDTO;
import uz.repay.payment.model.dto.core.pcMaintenance.request.PCMaintenanceRequestDTO;
import uz.repay.payment.model.dto.core.pcMaintenance.response.ABSSettingResponseDTO;
import uz.repay.payment.model.dto.core.pcMaintenance.response.PCMaintenanceResponseDTO;
import uz.repay.payment.model.entity.PCMaintenance;

import java.util.List;
import java.util.Optional;

public interface PCMaintenanceService {
    ResultData<List<PCMaintenanceResponseDTO>> findAll();

    ResultData<PCMaintenanceResponseDTO> create(@NotNull PCMaintenanceRequestDTO requestDTO);

    ResultData<PCMaintenanceResponseDTO> update(@NotNull Long id, @NotNull PCMaintenanceRequestDTO requestDTO);

    ResultData<List<ABSSettingResponseDTO>> findSettingByPC(@NotNull Long pcId);

    ResultData<PCMaintenanceResponseDTO> addABSSettingToPCMaintenance(
            @NotNull Long pcId,
            @NotNull PCMaintenanceAddSettingsRequestDTO requestDTO);

    ResultData<PCMaintenanceResponseDTO> update(@NotNull Long id, @NotNull PCMaintenanceAddSettingsRequestDTO requestDTO);

    ResultData<PCMaintenanceResponseDTO> deleteABSSettingById(@NotNull Long pcMId, @NotNull Long settingId);

    Optional<PCMaintenance> findByProcessing(Processing processing);

    ResultData<CoreError> cacheEvict();

    ResultData<PCMaintenanceResponseDTO> deleteById(@NotNull Long id);
}
