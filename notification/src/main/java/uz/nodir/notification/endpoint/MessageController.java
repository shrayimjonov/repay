package uz.nodir.notification.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.nodir.notification.model.dto.message.request.NotificationBaseRequestDTO;
import uz.nodir.notification.model.dto.message.request.NotificationDebtMessageRequestDTO;
import uz.nodir.notification.model.dto.message.request.NotificationMobileLinkRequestDTO;
import uz.nodir.notification.service.business.MessageService;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
@Tag(name = "message-endpoints")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/debt")
    @Operation(summary = "Debt message", description = "Send message about debtor debt", operationId = "notification-message-debt")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_DEBT_SMS)")
    public ResponseEntity<ResultData<CoreError>> debt(@RequestBody @Valid NotificationDebtMessageRequestDTO requestDTO) {
        return ResponseEntity.ok(messageService.debt(requestDTO));
    }

    @PostMapping("/mib")
    @Operation(summary = "MIB message", description = "Send mib message", operationId = "notification-message-mib")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_MIB_SMS)")
    public ResponseEntity<ResultData<CoreError>> mib(@RequestBody @Valid NotificationBaseRequestDTO requestDTO) {
        return ResponseEntity.ok(messageService.mib(requestDTO));
    }

    @PostMapping("/notice")
    @Operation(summary = "Notice message", description = "Send alert message before MIB message", operationId = "notification-message-notice")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_NOTICE_SMS)")
    public ResponseEntity<ResultData<CoreError>> notice(@RequestBody @Valid NotificationBaseRequestDTO requestDTO) {
        return ResponseEntity.ok(messageService.notice(requestDTO));
    }

    @PostMapping("/mobile-link")
    @Operation(summary = "Mobile link", description = "Send message with deep link to mobile", operationId = "notification-mobile-deep-link")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_MOBILE_LINK_SMS)")
    public ResponseEntity<ResultData<CoreError>> deepLink(@RequestBody @Valid NotificationMobileLinkRequestDTO requestDTO) {
        return ResponseEntity.ok(messageService.mobileDeepLink(requestDTO));
    }

}
