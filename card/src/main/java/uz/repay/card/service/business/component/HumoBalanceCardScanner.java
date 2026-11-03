package uz.repay.card.service.business.component;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.HuMoMiddlewareFeignClient;
import uz.repay.card.model.dto.huMoMiddleware.request.HuMoMiddlewareBaseRequest;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoCardV3InfoRequestParam;
import uz.repay.card.model.dto.humo.HumoBalanceDetectedEvent;
import uz.repay.card.model.dto.humo.HumoBalanceScanCard;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.service.core.component.broker.HumoBalanceScanKafkaProducer;
import uz.repay.card.util.HumoBalanceEligibilityUtils;
import uz.repay.card.util.HumoBalanceScanConstants;
import uz.repay.card.util.SystemTool;
import uz.repay.common.utils.CardUtils;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HumoBalanceCardScanner {
    private final HuMoMiddlewareFeignClient humoClient;
    private final HumoBalanceScanKafkaProducer kafkaProducer;
    private final HumoBalanceScanPartitionService partitionService;

    public void scan(HumoBalanceScanCard card, int partition, long assignmentVersion) {
        if (!isValid(card)) {
            log.warn("event=humo.balance.scan.skipped reason=invalid_card partition={} cardId={}",
                    partition, card != null ? card.getCardId() : null);
            return;
        }

        if (!partitionService.ownsCard(card.getCardId(), assignmentVersion)) {
            log.debug("event=humo.balance.scan.skipped reason=partition_not_owned cardId={} partition={}",
                    card.getCardId(), partition);
            return;
        }

        var startNanos = System.nanoTime();
        try {
            var pan = CardUtils.decryptPan(card.getEncryptedPan());
            if (pan.isBlank()) {
                log.warn("event=humo.balance.scan.skipped reason=empty_pan cardId={}", card.getCardId());
                return;
            }

            var cardInfo = fetchHumoCardInfo(pan, card);
            if (!HumoBalanceEligibilityUtils.isEligibleForAutodebit(
                    cardInfo,
                    HumoBalanceScanConstants.MINIMUM_HUMO_BALANCE_FOR_AUTODEBIT
            )) {
                return;
            }

            var event = new HumoBalanceDetectedEvent(
                    UUID.randomUUID().toString(),
                    card.getCardId(),
                    card.getEncryptedPan(),
                    resolveMaskedPan(card, cardInfo),
                    System.currentTimeMillis(),
                    HumoBalanceEligibilityUtils.availableAmount(cardInfo)
            );

            if (!partitionService.ownsCard(card.getCardId(), assignmentVersion)) {
                log.debug("event=humo.balance.scan.detected.skipped reason=partition_not_owned cardId={} partition={}",
                        card.getCardId(), partition);
                return;
            }

            if (kafkaProducer.publishDetectedEvent(event)) {
                log.info("event=humo.balance.scan.detected cardId={} maskedPan={} partition={} amount={} durationMs={}",
                        card.getCardId(), event.getMaskedPan(), partition, event.getAvailableAmount(),
                        elapsedMillis(startNanos));
            }
        } catch (FeignException error) {
            log.warn("event=humo.balance.scan.middleware_failed cardId={} maskedPan={} partition={} status={} message={}",
                    card.getCardId(), card.getMaskedPan(), partition, error.status(), safeMessage(error));
        } catch (RuntimeException error) {
            log.error("event=humo.balance.scan.exception cardId={} maskedPan={} partition={} message={}",
                    card.getCardId(), card.getMaskedPan(), partition, error.getMessage(), error);
        }
    }

    private HumoCardInfoResponse.HumoCardResult fetchHumoCardInfo(String pan, HumoBalanceScanCard card) {
        var request = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                new HuMoCardV3InfoRequestParam(new HumoCardInfoRequest(pan))
        );
        var startNanos = System.nanoTime();
        var response = humoClient.getCustomerCardInfo(request);
        if (response == null) {
            throw new IllegalStateException("HUMO card info response is empty");
        }

        if (response.getError() != null) {
            throw new IllegalStateException("HUMO card info error: " + response.getError().getMessage());
        }

        log.debug("event=humo.balance.scan.middleware.completed cardId={} maskedPan={} hasResult={} durationMs={}",
                card.getCardId(), card.getMaskedPan(), response.getResult() != null, elapsedMillis(startNanos));
        return response.getResult();
    }

    private String resolveMaskedPan(HumoBalanceScanCard card, HumoCardInfoResponse.HumoCardResult cardInfo) {
        var detectedMaskedPan = HumoBalanceEligibilityUtils.maskedPan(cardInfo);
        if (detectedMaskedPan != null && !detectedMaskedPan.isBlank()) {
            return detectedMaskedPan;
        }
        return card.getMaskedPan();
    }

    private boolean isValid(HumoBalanceScanCard card) {
        return card != null
                && card.getCardId() != null
                && card.getEncryptedPan() != null
                && !card.getEncryptedPan().isBlank();
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private String safeMessage(FeignException error) {
        if (error == null || error.getMessage() == null) {
            return null;
        }

        var message = error.getMessage().replace('\n', ' ').replace('\r', ' ').trim();
        var maxLength = 300;
        return message.length() > maxLength ? message.substring(0, maxLength) : message;
    }
}
