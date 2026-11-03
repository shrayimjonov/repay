package uz.nodir.file.service.business.impl;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.file.model.dto.location.request.LocationBaseRequestDTO;
import uz.nodir.file.model.dto.location.request.LocationFilterRequestDTO;
import uz.nodir.file.model.dto.location.response.LocationFilterResponseDTO;
import uz.nodir.file.model.entity.Location;
import uz.nodir.file.service.business.LocationService;
import uz.nodir.file.service.core.LocationDAO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.PageUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationDAO locationDAO;

    @Override
    public ResultData<CoreError> create(@Nonnull LocationBaseRequestDTO requestDTO) {
        log.info("Received command to create location with request: {}", requestDTO);


        var preparedEntity = new Location(
                requestDTO.getLatitude(),
                requestDTO.getLongitude(),
                requestDTO.getLoanExtId(),
                requestDTO.getDescription()
        );

        locationDAO.save(preparedEntity);

        var result = ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );

        log.info("Finished command to create location with response: {}", result);
        return result;
    }


    @Override
    public ResultData<LocationFilterResponseDTO> findAll(@Nonnull LocationFilterRequestDTO requestDTO) {
        log.info("Received command to find all with request: {}", requestDTO);
        var pageable = PageUtils.pageableBuilder(requestDTO);

        var pageResult = locationDAO.findAllByLoanExtId(requestDTO.getLoanExtId(), pageable);

        var result = ResultData.ok(
                new LocationFilterResponseDTO(pageResult)
        );

        log.info("Finished command to find all with response: {}", result.getResult().getData().size());
        return result;
    }


    @Override
    public ResultData<CoreError> updateById(@Nonnull Long id, @Nonnull LocationBaseRequestDTO requestDTO) {
        log.info("Received command to update location by id: {} with request: {}", id, requestDTO);

        var locationResult = locationDAO.findById(id);

        if (locationResult.isEmpty()) {
            var message = "Location with id: %d not found!".formatted(id);
            log.error(message);
            return new ResultData<>(ResultState.EntityNotFound, message);
        }

        var location = locationResult.get();

        location.setLongitude(requestDTO.getLongitude());
        location.setLatitude(requestDTO.getLatitude());
        location.setDescription(requestDTO.getDescription());
        locationDAO.save(location);

        var result = ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );

        log.info("Finished command to update location with response: {}", result);
        return result;
    }


    @Override
    public ResultData<CoreError> deleteById(@Nonnull Long id) {
        log.info("Received command to delete location by id: {}", id);

        var locationResult = locationDAO.findById(id);

        if (locationResult.isEmpty()) {
            var message = "Location with id: %d not found!".formatted(id);
            log.error(message);
            return new ResultData<>(ResultState.EntityNotFound, message);
        }

        locationDAO.deleteById(id);

        var result = ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );

        log.info("Finished command to delete location with response: {}", result);
        return result;
    }


}
