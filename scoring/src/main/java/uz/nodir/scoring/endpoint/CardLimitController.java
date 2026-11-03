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
import uz.nodir.scoring.model.dto.limit.request.CardLimitRequestDTO;
import uz.nodir.scoring.model.dto.limit.response.CardLimitResponseDTO;
import uz.nodir.scoring.service.business.main.CardLimitService;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/card-limit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "card-limit-endpoint")
public class CardLimitController {
    private final CardLimitService cardLimitService;

    @PostMapping
    @Operation(summary = "Card limit", description = "Take card all limits", operationId = "scoring-card-limit")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_CARD_LIMIT)")
    public ResponseEntity<ResultData<List<CardLimitResponseDTO>>> getCardLimits(@RequestBody @Valid CardLimitRequestDTO requestDTO) {
        return ResponseEntity.ok(cardLimitService.getCardLimits(requestDTO));
    }
}
