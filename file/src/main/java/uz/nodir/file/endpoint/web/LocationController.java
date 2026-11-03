package uz.nodir.file.endpoint.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.nodir.file.model.dto.location.request.LocationBaseRequestDTO;
import uz.nodir.file.model.dto.location.request.LocationFilterRequestDTO;
import uz.nodir.file.model.dto.location.response.LocationFilterResponseDTO;
import uz.nodir.file.service.business.LocationService;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

@RestController
@RequestMapping(value = "/api/v1/location", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "location-methods")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    @Operation(summary = "Location note", description = "Add location for loan", operationId = "file-location-create")
    public ResponseEntity<ResultData<CoreError>> create(@RequestBody @Valid LocationBaseRequestDTO requestDTO) {
        return ResponseEntity.ok(locationService.create(requestDTO));
    }

    @PostMapping("/filter")
    @Operation(summary = "Find locations by LoanExtId", description = "Find locations list by loanExtId", operationId = "file-location-byLoan")
    public ResponseEntity<ResultData<LocationFilterResponseDTO>> findLoanLocations(@RequestBody @Valid LocationFilterRequestDTO requestDTO) {
        return ResponseEntity.ok(locationService.findAll(requestDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Location update", description = "Update location by noteId", operationId = "file-location-update-byId")
    public ResponseEntity<ResultData<CoreError>> updateById(@PathVariable("id") Long id, @RequestBody @Valid LocationBaseRequestDTO requestDTO) {
        return ResponseEntity.ok(locationService.updateById(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Location delete", description = "Delete location by noteId", operationId = "file-location-delete-byId")
    public ResponseEntity<ResultData<CoreError>> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(locationService.deleteById(id));
    }


}
