package uz.nodir.notification.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.nodir.notification.model.dto.push.response.PushV2ResponseDTO;
import uz.nodir.notification.service.business.PushService;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/push", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "push-methods")
public class PushController {
    private final PushService pushService;

    @GetMapping("/byUser")
    @Operation(summary = "Push by user", description = "Get user last 7 day notifications", operationId = "find-push-by-user")
    public ResponseEntity<ResultData<List<PushV2ResponseDTO>>> byUser() {
        return ResponseEntity.ok(pushService.findUserPushes());
    }

    @PutMapping("/mark")
    @Operation(summary = "Mark pushes", description = "Mark all pushes read by user", operationId = "mark-push-by-user")
    public ResponseEntity<ResultData<CoreError>> markUserPushes() {
        return ResponseEntity.ok(pushService.checkReadPushes());
    }

}
