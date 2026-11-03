package uz.repay.card.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.autopay.request.AutoPayPushRequestDTO;
import uz.repay.card.service.business.component.AutoPayPushComponent;
import uz.repay.card.service.business.component.AutoPayToSVGateConverter;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;

@RestController
@RequestMapping("/api/v1/auto-pay")
@RequiredArgsConstructor
public class AutoPayController {
    private final AutoPayPushComponent autoPayPushComponent;
    private final AutoPayToSVGateConverter autoPayToSVGateConverter;

    @PostMapping("/balance/alert")
    public ResponseEntity<?> balanceAlert(@RequestBody AutoPayPushRequestDTO requestDTO) {
        return ResponseEntity.ok(autoPayPushComponent.pushBalance(requestDTO));
    }

    @PatchMapping("/checkSVGate")
    public ResponseEntity<?> checkSVGateCards() {
        autoPayToSVGateConverter.checkAutoPayCardsToSVGate();
        return ResponseEntity.ok(
                ResultData.ok(
                        new CoreError(0, ResponseStatus.SUCCESS.name())
                )
        );
    }
}
