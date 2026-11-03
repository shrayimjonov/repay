package uz.repay.card.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.card.OTPRequestDTO;
import uz.repay.card.model.dto.core.request.card.VerifyRequestDTO;
import uz.repay.card.service.business.OneTimeCardDataService;
import uz.repay.card.service.business.component.FirstTimeCardFinderComponent;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;

@RestController
@RequestMapping(value = "/api/v1/otc", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "one-time-card-add-endpoint")
public class OneTimeCardDataController {
    private final OneTimeCardDataService oneTimeCardDataService;
    private final FirstTimeCardFinderComponent firstTimeCardFinderComponent;

    @PostMapping("/verify")
    @Operation(summary = "OneTime verify", description = "One time card adder verify method", operationId = "one-time-verify")
    public ResponseEntity<?> verifyCard(@RequestBody @NotNull OTPRequestDTO requestDTO) {
        return ResponseEntity.ok(oneTimeCardDataService.verify(requestDTO));
    }


    @PostMapping("/confirmNotFamiliar")
    @Operation(summary = "OneTime not familiar confirm", description = "One time card adder confirm not familiar cards", operationId = "one-time-confirm-not-familiar")
    public ResponseEntity<?> confirmNotFamiliarCardAndGetCardInfo(@RequestBody @NotNull VerifyRequestDTO requestDTO) {
        return ResponseEntity.ok(oneTimeCardDataService.confirmNotFamiliar(requestDTO));
    }

    @PostMapping("/confirmFamiliar")
    @Operation(summary = "OneTime familiar confirm", description = "One time card adder confirm familiar cards", operationId = "one-time-confirm-familiar")
    public ResponseEntity<?> confirmFamiliarCardAndGetCardInfo(@RequestBody @NotNull VerifyRequestDTO requestDTO) {
        return ResponseEntity.ok(oneTimeCardDataService.confirmFamiliar(requestDTO));
    }

    @PatchMapping("/firstTimeCardFound")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    public ResponseEntity<?> firstTimeCardFound(@RequestParam("loanExtId") String loanExtId) {
        firstTimeCardFinderComponent.findCardFirstTimeAfterLoanAdded(loanExtId);
        return ResponseEntity.ok(
                ResultData.ok(new CoreError(0, ResponseStatus.SUCCESS.name()))
        );
    }
}
