package uz.nodir.notification.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.nodir.notification.model.dto.protocol.request.ProtocolFilterRequestDTO;
import uz.nodir.notification.model.dto.protocol.response.ProtocolFilterResponseDTO;
import uz.nodir.notification.service.business.SMSProtocolService;
import uz.repay.common.model.dto.response.core.ResultData;

@RestController
@RequestMapping("/api/v1/protocol")
@RequiredArgsConstructor
@Tag(name = "sms-protocol-endpoints")
public class ProtocolController {
    private final SMSProtocolService smsProtocolService;

    @PostMapping("/filter")
    @Operation(summary = "Filter sms protocol", description = "Find sent messages by filter params", operationId = "protocol-filter")
    public ResponseEntity<ResultData<ProtocolFilterResponseDTO>> filter(@RequestBody @Valid ProtocolFilterRequestDTO requestDTO) {
        return ResponseEntity.ok(smsProtocolService.findMessagesByFilter(requestDTO));
    }

}
