package uz.nodir.scoring.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.nodir.scoring.model.dto.history.request.CardHistoryRequestDTO;
import uz.nodir.scoring.model.dto.history.response.CardHistoryResponseDTO;
import uz.nodir.scoring.service.business.main.MonitoringService;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/monitoring", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "monitoring-endpoints")
public class MonitoringController {
    private final MonitoringService monitoringService;

    @PostMapping("/history")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_CARD_MONITORING)")
    @Operation(summary = "Card monitoring", description = "To show card monitoring for 30 days", operationId = "scoring-card-monitoring")
    public ResponseEntity<ResultData<List<CardHistoryResponseDTO>>> history(@RequestBody @Valid CardHistoryRequestDTO requestDTO) {
        return ResponseEntity.ok(monitoringService.takeCardHistory(requestDTO));
    }
}
