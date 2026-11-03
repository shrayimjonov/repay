package uz.nodir.scoring.service.business.main.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.scoring.model.dto.limit.request.LimitBaseRequestDTO;
import uz.nodir.scoring.model.dto.limit.response.LimitBaseResponseDTO;
import uz.nodir.scoring.model.entity.Limit;
import uz.nodir.scoring.service.business.main.LimitService;
import uz.nodir.scoring.service.core.LimitDAO;
import uz.repay.common.model.dto.request.card.response.StatusResponseDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {
    private final LimitDAO limitDAO;

    @Override
    public ResultData<CoreError> create(@NotNull LimitBaseRequestDTO requestDTO) {
        log.info("Received command to create limit with request dto: {}", requestDTO);

        var limitResult = limitDAO.findByType(requestDTO.getType());

        if (limitResult.isPresent()) {
            var message = "Limit with type %s already exists".formatted(requestDTO.getType());
            log.error("Finished command. Cause: {}", message);
            return new ResultData<>(
                    ResultState.BlockOperation,
                    message
            );
        }

        var limit = new Limit(
                requestDTO.getType(),
                requestDTO.getDescription(),
                requestDTO.getProcessing()
        );

        limitDAO.save(limit);

        var responseDTO = new ResultData<>(
                new CoreError(
                        0,
                        ResponseStatus.SUCCESS.name()
                )
        );

        log.info("Finished command to create limit with response dto: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public ResultData<CoreError> update(@NotNull Long id, @NotNull LimitBaseRequestDTO requestDTO) {
        log.info("Received command to update limit by id: {} with request dto: {}", id, requestDTO);

        var limitResult = limitDAO.findById(id);

        if (limitResult.isEmpty()) {
            var message = "Limit with id %s not found".formatted(id);
            log.error("Finished command. Cause: {}", message);
            return new ResultData<>(
                    ResultState.EntityNotFound,
                    message
            );
        }

        updateLimitFields(requestDTO, limitResult.get());

        var responseDTO = new ResultData<>(
                new CoreError(
                        0,
                        ResponseStatus.SUCCESS.name()
                )
        );

        log.info("Finished command to update limit with response dto: {}", responseDTO);
        return responseDTO;

    }

    private void updateLimitFields(LimitBaseRequestDTO requestDTO, Limit limit) {
        limit.setType(requestDTO.getType());
        limit.setDescription(requestDTO.getDescription());
        limit.setProcessing(requestDTO.getProcessing());
        limitDAO.save(limit);
    }


    @Override
    public ResultData<List<LimitBaseResponseDTO>> findAll() {
        log.info("Received command to find all limit list");

        var limits = limitDAO.getAll()
                .stream()
                .map(LimitBaseResponseDTO::new)
                .toList();

        var responseDTO = new ResultData<>(limits);

        log.info("Finished command to find all limit list with size {}", limits.size());
        return responseDTO;
    }

    @Override
    public ResultData<CoreError> deleteById(@NotNull Long id) {
        log.info("Received command to delete limit by id: {}", id);

        var limitResult = limitDAO.getAll()
                .stream()
                .filter(s-> s.getId().equals(id))
                .findFirst();

        if (limitResult.isEmpty()) {
            var message = "Limit with id: %d not found".formatted(id);
            log.error("Finished command. Cause: {}", message);
            return new ResultData<>(
                    ResultState.EntityNotFound,
                    message
            );
        }

        limitDAO.deleteById(id);

        var responseDTO = new ResultData<>(
                new CoreError(
                        0,
                        ResponseStatus.SUCCESS.name()
                )
        );

        log.info("Finished command to delete limit with response dto: {}", responseDTO);
        return responseDTO;
    }


    @Override
    public ResultData<CoreError> evictCache() {
        log.info("Received command to evict cache");

        limitDAO.cacheEvict();
        var responseDTO = new ResultData<>(
                new CoreError(
                        0,
                        ResponseStatus.SUCCESS.name()
                )
        );

        log.info("Finished command to limit cache evict with response dto: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public ResultData<LimitBaseResponseDTO> findById(@NotNull Long id) {
        log.info("Received command to find limit by id: {}", id);
        var limitResult = limitDAO.getAll()
                .stream()
                .filter(s-> s.getId().equals(id))
                .findFirst();

        if (limitResult.isEmpty()) {
            var message = "Limit with id: %d not found".formatted(id);
            log.error("Finished command. Cause: {}", message);
            return new ResultData<>(
                    ResultState.EntityNotFound,
                    message
            );
        }

        var responseDTO = new ResultData<>(
                new LimitBaseResponseDTO(limitResult.get())
        );

        log.info("Finished command to find limit by id with response dto: {}", responseDTO);
        return responseDTO;
    }


}
