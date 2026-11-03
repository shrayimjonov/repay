package uz.nodir.scoring.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.nodir.scoring.model.dto.limit.request.LimitBaseRequestDTO;
import uz.nodir.scoring.model.dto.limit.response.LimitBaseResponseDTO;
import uz.nodir.scoring.service.business.main.LimitService;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/limit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "limits-endpoint")
@PreAuthorize("hasAnyRole(T(uz.repay.common.model.enums.UserPermission).REPAY_LIMIT_MODER)")
public class LimitController {
    private final LimitService limitService;

    @PostMapping
    @Operation(summary = "Create limit", description = "Add new limit type", operationId = "scoring-limit-create")
    public ResponseEntity<ResultData<CoreError>> create(@RequestBody @Valid LimitBaseRequestDTO requestDTO) {
        return ResponseEntity.ok(limitService.create(requestDTO));
    }

    @GetMapping
    @Operation(summary = "List of limits", description = "Find all limits", operationId = "scoring-limit-find-all")
    public ResponseEntity<ResultData<List<LimitBaseResponseDTO>>> findAll() {
        return ResponseEntity.ok(limitService.findAll());
    }

    @PostMapping("/{id}")
    @Operation(summary = "Update limit", description = "Update limit by id", operationId = "scoring-limit-update-by-id")
    public ResponseEntity<ResultData<CoreError>> updateById(@PathVariable Long id, @RequestBody @Valid LimitBaseRequestDTO requestDTO) {
        return ResponseEntity.ok(limitService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete limit", description = "Delete limit by id", operationId = "scoring-limit-delete-by-id")
    public ResponseEntity<ResultData<CoreError>> deleteById(@PathVariable Long id) {
        return ResponseEntity.ok(limitService.deleteById(id));
    }

    @PatchMapping("/cacheEvict")
    @Operation(summary = "Cache evict of limit", description = "Clear limits cache", operationId = "scoring-limit-cache-evict")
    public ResponseEntity<ResultData<CoreError>> cacheEvict() {
        return ResponseEntity.ok(limitService.evictCache());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find limit", description = "Find limit by id", operationId = "scoring-limit-find-by-id")
    public ResponseEntity<ResultData<LimitBaseResponseDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(limitService.findById(id));
    }
}
