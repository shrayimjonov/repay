package uz.repay.card.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.card.model.dto.core.request.card.OTPRequestDTO;
import uz.repay.card.model.dto.core.request.card.VerifyRequestDTO;
import uz.repay.card.service.business.VerifyCardService;

@RestController
@RequestMapping("/api/v1/verify")
@RequiredArgsConstructor
@PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ADD_CARD)")
public class VerifyController {
    private final VerifyCardService verifyCardService;

    @PostMapping("/otp")
    public ResponseEntity<?> sendOTP(@RequestBody @Valid OTPRequestDTO dto) {
        return ResponseEntity.ok(verifyCardService.sendOTP(dto));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmOTP(@RequestBody @Valid VerifyRequestDTO dto) {
        return ResponseEntity.ok(verifyCardService.verify(dto));
    }
}
