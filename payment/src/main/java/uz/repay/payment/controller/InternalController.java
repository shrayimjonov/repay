package uz.repay.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.request.backlash.BacklashCreateRequestDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.payment.service.business.BacklashInterService;
import uz.repay.payment.service.business.PaymentServiceV2;

@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
public class InternalController {
    private final PaymentServiceV2 paymentServiceV2;
    private final BacklashInterService backlashInterService;

    @PostMapping("/pay")
    @Deprecated
    public ResponseEntity<?> pay(@RequestBody @Valid PayRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentServiceV2.payV3(requestDTO, 2));
    }

    @PostMapping("/backlash")
    public ResponseEntity<?> createBacklash(@RequestBody @Valid BacklashCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(backlashInterService.createBacklash(requestDTO));
    }


}
