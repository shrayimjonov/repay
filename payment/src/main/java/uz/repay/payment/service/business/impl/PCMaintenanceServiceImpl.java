package uz.repay.payment.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.payment.model.dto.core.pcMaintenance.request.PCMaintenanceAddSettingsRequestDTO;
import uz.repay.payment.model.dto.core.pcMaintenance.request.PCMaintenanceRequestDTO;
import uz.repay.payment.model.dto.core.pcMaintenance.response.ABSSettingResponseDTO;
import uz.repay.payment.model.dto.core.pcMaintenance.response.PCMaintenanceResponseDTO;
import uz.repay.payment.model.entity.ABSSetting;
import uz.repay.payment.model.entity.PCMaintenance;
import uz.repay.payment.service.business.PCMaintenanceService;
import uz.repay.payment.service.core.PCMaintenanceDAO;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PCMaintenanceServiceImpl implements PCMaintenanceService {
    private final PCMaintenanceDAO dao;

    @Override
    public Optional<PCMaintenance> findByProcessing(Processing processing) {
        log.info("Find pc maintenance find by processing: {}", processing);
        return dao
                .getAll()
                .stream()
                .filter(p -> p.getProcessing().equals(processing))
                .findFirst();
    }

    @Override
    public ResultData<List<PCMaintenanceResponseDTO>> findAll() {
        log.info("Find all pc maintenances");
        return ResultData.ok(
                dao.getAll()
                        .stream()
                        .map(PCMaintenanceResponseDTO::new)
                        .toList()
        );

    }

    @Override
    public ResultData<PCMaintenanceResponseDTO> create(@NotNull PCMaintenanceRequestDTO requestDTO) {
        log.info("Create pc maintenance for pc: {}", requestDTO);

        var pcOptional = dao.getAll()
                .stream()
                .filter(z -> z.getProcessing().equals(requestDTO.getProcessing()))
                .findFirst();

        if (pcOptional.isPresent()) {
            log.error("PC maintenance for processing: {} already exist!", requestDTO.getProcessing());
            return new ResultData<>(1300, "PC Maintenance already exist");
        }

        var entity = new PCMaintenance(
                requestDTO.getProcessing()
        );

        var pcMaintenance = dao.save(entity);

        return ResultData.ok(
                new PCMaintenanceResponseDTO(
                        pcMaintenance
                )
        );
    }

    @Override
    public ResultData<PCMaintenanceResponseDTO> update(Long id, PCMaintenanceRequestDTO requestDTO) {
        log.info("Received command update pc maintenance by id: {} with request: {}", id, requestDTO);

        var pcMaintenanceOptional = findById(id);

        if (pcMaintenanceOptional.isEmpty()) {
            log.error("PC maintenance with id: {} not found", id);
            return new ResultData<>(1301, "PC Maintenance with id: %d not found".formatted(id));
        }

        var pc = pcMaintenanceOptional.get();

        if (dao
                .getAll()
                .stream()
                .anyMatch(s -> s.getProcessing().equals(requestDTO.getProcessing())))
            return new ResultData<>(ResultState.NotProcessed, "PC: %s already exist!".formatted(requestDTO.getProcessing()));


        pc.setProcessing(requestDTO.getProcessing());

        var pcMaintenance = dao.save(pc);

        var response = ResultData.ok(
                new PCMaintenanceResponseDTO(
                        pcMaintenance
                )
        );

        log.info("Finished command update pc maintenance by id with dto: {}", response);
        return response;
    }

    @Override
    public ResultData<PCMaintenanceResponseDTO> addABSSettingToPCMaintenance(
            @NotNull Long pcId,
            @NotNull PCMaintenanceAddSettingsRequestDTO requestDTO) {
        log.info("Add abs setting: {} to pcMaintenance: {}", requestDTO, pcId);

        var pcMaintenanceOptional = findById(pcId);

        if (pcMaintenanceOptional.isEmpty())
            throw new EntityNotFoundException("PC Maintenance with id: %d not found".formatted(pcId));

        var pcM = pcMaintenanceOptional.get();

        if (pcM
                .getAbsSettings()
                .stream()
                .anyMatch(p -> p.getOrder().equals(requestDTO.getOrder())))
            throw new InvalidDataException("PC Maintenance: %d with order id: %d already defined".formatted(pcId, requestDTO.getOrder()));


        var absSetting = absSettingBuilder(requestDTO, pcM);


        pcM.getAbsSettings()
                .add(absSetting);

        dao.save(pcM);

        return ResultData.ok(
                new PCMaintenanceResponseDTO(
                        pcM
                )
        );
    }

    private static ABSSetting absSettingBuilder(PCMaintenanceAddSettingsRequestDTO requestDTO, PCMaintenance pcM) {
        var absSetting = new ABSSetting(
                requestDTO,
                pcM
        );
        log.info("Prepared abs Setting entity: {}", absSetting);
        return absSetting;
    }


    private Optional<PCMaintenance> findById(Long id) {
        log.info("Find pc maintenance find by processing id: {}", id);
        return dao
                .getAll()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }


    @Override
    public ResultData<List<ABSSettingResponseDTO>> findSettingByPC(@NotNull Long pcId) {
        log.info("Find settings by pc id: {}", pcId);

        var absSettingsOptional = findById(pcId);

        return absSettingsOptional.map(
                pcMaintenance ->
                        ResultData.ok(
                                pcMaintenance
                                        .getAbsSettings()
                                        .stream()
                                        .map(ABSSettingResponseDTO::new)
                                        .toList()

                        )
        ).orElseGet(
                () -> new ResultData<>(
                        List.of()
                )
        );
    }


    @Override
    public ResultData<PCMaintenanceResponseDTO> update(@NotNull Long id, @NotNull PCMaintenanceAddSettingsRequestDTO requestDTO) {
        log.info("Update pc maintenance for id: {}", id);

        var pcMaintenanceOptional = findById(requestDTO.getPcMaintenanceId());

        if (pcMaintenanceOptional.isEmpty())
            throw new EntityNotFoundException("PC Maintenance not found with id: %d".formatted(requestDTO.getPcMaintenanceId()));


        var pcM = pcMaintenanceOptional.get();

        var pcMSettingOptional = pcM
                .getAbsSettings()
                .stream()
                .filter(z -> z.getId().equals(id))
                .findFirst();

        if (pcMSettingOptional.isEmpty())
            throw new InvalidDataException("PC Maintenance: %d with abs setting id: %d not found".formatted(requestDTO.getPcMaintenanceId(), id));

        var pcMSetting = pcMSettingOptional.get();

        updateFields(requestDTO, pcMSetting);

        dao.save(pcM);

        return ResultData.ok(
                new PCMaintenanceResponseDTO(
                        pcM
                )
        );
    }


    private static void updateFields(PCMaintenanceAddSettingsRequestDTO requestDTO, ABSSetting pcMSetting) {
        pcMSetting.setBranchDebit(requestDTO.getBranchDebit());
        pcMSetting.setAccountDebit(requestDTO.getAccountDebit());
        pcMSetting.setBranchCredit(requestDTO.getBranchCredit());
        pcMSetting.setAccountCredit(requestDTO.getAccountCredit());
        pcMSetting.setPurpose(requestDTO.getPurpose());
        pcMSetting.setPurposeCode(requestDTO.getPurposeCode());
        pcMSetting.setDocType(requestDTO.getDocType());
        pcMSetting.setOrder(requestDTO.getOrder());
        pcMSetting.setCurrency(requestDTO.getCurrency());
    }


    @Override
    public ResultData<PCMaintenanceResponseDTO> deleteABSSettingById(@NotNull Long pcMId, @NotNull Long settingId) {
        log.info("Delete abs setting by id: {} from pc maintenance: {}", settingId, pcMId);

        var pcMOptional = findById(pcMId);

        if (pcMOptional.isEmpty())
            throw new EntityNotFoundException("PC Maintenance not found with id: %d".formatted(pcMId));

        var pcM = pcMOptional.get();

        pcM
                .getAbsSettings()
                .removeIf(z -> z.getId().equals(settingId));

        dao.save(pcM);

        return ResultData.ok(
                new PCMaintenanceResponseDTO(
                        pcM
                )
        );
    }

    @Override
    public ResultData<CoreError> cacheEvict() {
        log.info("PC Maintenance cache evict");
        dao.cacheEvict();
        return ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );
    }

    @Override
    public ResultData<PCMaintenanceResponseDTO> deleteById(Long id) {
        log.info("Received command to delete pc maintenance by id: {}", id);
        var pcMOptional = findById(id);

        if (pcMOptional.isEmpty()) {
            var message = "PC Maintenance not found by id: %d".formatted(id);
            log.error(message);
            return new ResultData<>(ResultState.NotProcessed, message);
        }

        var pcM = pcMOptional.get();
        dao.deleteById(id);

        var response = ResultData.ok(new PCMaintenanceResponseDTO(pcM));

        log.info("Finished command delete pc maintenance by id with dto: {}", response);
        return response;
    }
}
