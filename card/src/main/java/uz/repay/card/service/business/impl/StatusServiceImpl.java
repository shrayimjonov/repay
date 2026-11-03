package uz.repay.card.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.status.StatusRequestDTO;
import uz.repay.card.model.entity.Status;
import uz.repay.card.service.business.StatusService;
import uz.repay.card.service.core.StatusDAO;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.model.dto.request.card.response.StatusResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusServiceImpl implements StatusService {
    private final StatusDAO statusDAO;

    @Override
    public ResultData<StatusResponseDTO> create(@NonNull StatusRequestDTO requestDTO) {
        try {
            var entity = statusDAO.save(
                    dtoToEntity(new Status(), requestDTO)
            );

            return new ResultData<>(new StatusResponseDTO(
                    entity.getId(),
                    entity.getCode(),
                    entity.getDefinition(),
                    entity.getProcessing(),
                    entity.getType(),
                    entity.getDefinitionUz()
            ));
        } catch (DataIntegrityViolationException s) {
            return new ResultData<>(ResultState.SQLException, s.getMessage());
        }
    }

    @Override
    public ResultData<StatusResponseDTO> update(Long id, @NonNull StatusRequestDTO requestDTO) {
        log.info("Update status with id: {} ", id);
        var status = getStatusById(id);

        log.info("Updating from: {} to: {}", status, requestDTO);
        dtoToEntity(status, requestDTO);

        var savedStatus = statusDAO.save(status);
        return new ResultData<>(new StatusResponseDTO(
                savedStatus.getId(),
                savedStatus.getCode(),
                savedStatus.getDefinition(),
                savedStatus.getProcessing(),
                savedStatus.getType(),
                savedStatus.getDefinitionUz()
        ));
    }

    private Status getStatusById(Long id) {
        var statusOptional = statusDAO.getAll()
                .stream()
                .filter(z -> z.getId().equals(id))
                .findFirst();

        if (statusOptional.isEmpty())
            throw new EntityNotFoundException("Status with id: %d not found in database!".formatted(id));

        return statusOptional.get();
    }

    @Override
    public ResultData<StatusResponseDTO> findById(Long id) {
        log.info("Find status by id: {}", id);
        var status = getStatusById(id);
        return new ResultData<>(new StatusResponseDTO(
                status.getId(),
                status.getCode(),
                status.getDefinition(),
                status.getProcessing(),
                status.getType(),
                status.getDefinitionUz()
        ));
    }

    @Override
    public ResultData<List<StatusResponseDTO>> findAll(String definitionUz) {
        var statusList = statusDAO.getAll();

        if (definitionUz == null || definitionUz.isEmpty()) {
            log.info("Find all statuses without params...");

            return new ResultData<>(
                    statusList
                            .stream()
                            .map(z -> new StatusResponseDTO(
                                    z.getId(),
                                    z.getCode(),
                                    z.getDefinition(),
                                    z.getProcessing(),
                                    z.getType(),
                                    z.getDefinitionUz()
                            ))
                            .toList()
            );
        } else {
            log.info("Find all statuses with definition...: {}", definitionUz);

            return new ResultData<>(
                    statusList
                            .stream()
                            .filter(z -> z.getDefinitionUz().toUpperCase().contains(definitionUz.toUpperCase()))
                            .map(z -> new StatusResponseDTO(
                                    z.getId(),
                                    z.getCode(),
                                    z.getDefinition(),
                                    z.getProcessing(),
                                    z.getType(),
                                    z.getDefinitionUz()))
                            .toList()
            );
        }

    }

    @Override
    public ResultData<List<StatusResponseDTO>> findAll() {
        var statusList = statusDAO.getAll();
        return new ResultData<>(
                statusList
                        .stream()
                        .map(z -> new StatusResponseDTO(
                                z.getId(),
                                z.getCode(),
                                z.getDefinition(),
                                z.getProcessing(),
                                z.getType(),
                                z.getDefinitionUz()))
                        .toList());
    }


    @Override
    public ResultData<StatusResponseDTO> deleteById(Long id) {
        var status = getStatusById(id);
        statusDAO.delete(id);
        return new ResultData<>(new StatusResponseDTO(
                status.getId(),
                status.getCode(),
                status.getDefinition(),
                status.getProcessing(),
                status.getType(),
                status.getDefinitionUz()
        ));
    }

    public Status dtoToEntity(Status status, StatusRequestDTO dto) {
        status.setCode(dto.getCode());
        status.setDefinition(dto.getDefinition());
        status.setProcessing(dto.getProcessing());
        status.setType(dto.getType());
        status.setDefinitionUz(dto.getDefinitionUz());
        return status;
    }


    @Override
    public ResultData<String> evictCache() {
        statusDAO.cacheEvict();
        return new ResultData<>("CACHE EVICTED");
    }
}
