package uz.repay.payment.service.business.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.repay.common.exception.ServiceDisabledException;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;
import uz.repay.payment.model.dto.cardService.response.TerminalResponse;
import uz.repay.payment.service.core.SwitcherRedisService;
import uz.repay.payment.service.web.CardService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommonHelper {
    @Value("${spring.application.name}")
    private String name;

    private final SwitcherRedisService switcherRedisService;
    private final CardService cardService;
    private final ObjectMapper objectMapper;

    private static final String FALLBACK_TERMINAL_ID = "92903142";
    private static final String FALLBACK_MERCHANT_ID = "90510023948";

    public void checkSwitcherIsEnabled() {
        log.info("Check switcher: {}", name);
        var paymentSwitcher = switcherRedisService.findByName(name);

        log.info("{} Switcher status is: {}", name, paymentSwitcher);

        if (!paymentSwitcher.getIsEnabled()) {
            throw new ServiceDisabledException("%s switcher is turned off".formatted(name));
        }
    }

    public void checkSwitcherIsEnabled(String switcherName) {
        log.info("Check switcher: {}", switcherName);
        var paymentSwitcher = switcherRedisService.findByName(switcherName);

        log.info("{} Switcher status is: {}", switcherName, paymentSwitcher);

        if (!paymentSwitcher.getIsEnabled()) {
            throw new ServiceDisabledException("%s switcher is turned off".formatted(switcherName));
        }
    }

    public SimpleResponse<TerminalResponse> getEPosForBranch(Processing processing, String branch) {
        var terminalsResponse = cardService.getAllTerminals();

        if (!terminalsResponse.getStatus()) {
            log.error("Get terminal method has error: %s".formatted(terminalsResponse.getError().getMessage()));
            return new SimpleResponse<>(399, terminalsResponse.getError().getMessage());
        }


        var terminalOptional = terminalsResponse.getData()
                .stream()
                .filter(t -> t.getProcessing().equals(processing))
                .filter(t -> t.getService().equals(ServiceType.REPAY))
                .filter(t -> t.getBranch().getCode().equals(branch))
                .findFirst();

        if (terminalOptional.isEmpty()) {
            log.error("Terminal not found for processing: %s service: %s and branch: %s"
                    .formatted(processing, ServiceType.REPAY, branch));
            return new SimpleResponse<>(497, "Terminal not found for processing: %s service: %s and branch: %s"
                    .formatted(processing, ServiceType.REPAY, branch));
        }


        var ePos = terminalOptional.get();
        log.info("For branch: {} ePos is: {}", branch, ePos);
        return new SimpleResponse<>(ePos);
    }

    public SimpleResponse<TerminalResponse> getEPosForUzcardPayment(Processing processing, String branch, boolean withExceptional) {
        var terminalsResponse = cardService.getAllTerminals();
        try {
            log.info("EPOS_GET_LIST_RESPONSE: {}", objectMapper.writeValueAsString(terminalsResponse));
        } catch (JsonProcessingException e) {
            log.error(" Object writing error: {}", e.getMessage());
        }

        if (!terminalsResponse.getStatus()) {
            log.error("Get terminal method has error: %s".formatted(terminalsResponse.getError().getMessage()));
            return new SimpleResponse<>(399, terminalsResponse.getError().getMessage());
        }


        var terminals = terminalsResponse.getData()
                .stream()
                .filter(t -> t.getProcessing().equals(processing))
                .filter(t -> t.getService().equals(ServiceType.REPAY))
                .filter(t -> t.getBranch().getCode().equals(branch)).toList();
        var terminalResponseOptional = terminals.stream()
                .filter(t -> t.isExceptional() == withExceptional)
                .findFirst();

        if (terminalResponseOptional.isEmpty()) {
            if (withExceptional) {
                var fallback = terminals.stream().findFirst()
                        .map(t -> new TerminalResponse(t.getId(), t.getBranch(), t.getProcessing(),
                                FALLBACK_MERCHANT_ID, t.getService(), FALLBACK_TERMINAL_ID, t.getAccount(), true))
                        .orElseGet(() -> new TerminalResponse(null, null, processing,
                                FALLBACK_MERCHANT_ID, ServiceType.REPAY, FALLBACK_TERMINAL_ID, null, true));
                log.warn("No exceptional terminal for branch: {} -> fallback terminalId={} merchantId={}",
                        branch, FALLBACK_TERMINAL_ID, FALLBACK_MERCHANT_ID);
                return new SimpleResponse<>(fallback);
            }
            log.error("Terminal not found for processing: %s service: %s branch: %s exceptional: %s"
                    .formatted(processing, ServiceType.REPAY, branch, withExceptional));
            return new SimpleResponse<>(497, "Terminal not found for processing: %s service: %s and branch: %s"
                    .formatted(processing, ServiceType.REPAY, branch));
        }
        var ePos = terminalResponseOptional.get();
        log.info("For branch: {} exceptional: {} ePos is: {}", branch, withExceptional, ePos);
        return new SimpleResponse<>(ePos);
    }
}
