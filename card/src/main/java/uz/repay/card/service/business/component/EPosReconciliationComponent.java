package uz.repay.card.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.humoGateway.request.HuMoRecoActiveRequest;
import uz.repay.card.model.dto.humoGateway.request.HuMoRecoConfirmRequest;
import uz.repay.card.model.dto.humoGateway.response.HuMoRecoConfirmResponse;
import uz.repay.card.model.entity.EPos;
import uz.repay.card.model.entity.Reconciliation;
import uz.repay.card.model.property.PrefixProperty;
import uz.repay.card.service.core.EPosDAO;
import uz.repay.card.service.core.ReconciliationDAO;
import uz.repay.card.service.web.HuMoGatewayService;
import uz.repay.card.util.SystemTool;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResponseStatus;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class EPosReconciliationComponent {
    private final EPosDAO ePosDAO;
    private final PrefixProperty property;
    private final HuMoGatewayService huMoGatewayService;
    private final ReconciliationDAO reconciliationDAO;

    @Async
    @Scheduled(cron = "0 45 8,11,14,23 * * ?")
    @SchedulerLock(name = "reconciliationHuMoTerminals", lockAtMostFor = "PT1H", lockAtLeastFor = "PT30M")
    public void reconciliationHuMoTerminals() {
        log.info("Start huMo terminals reconciliation");
        synchronized (this) {
            var ePosList = ePosDAO.getAll();
            var sessionId = UUID.randomUUID();

            log.info("Session for reconciliation: {}", sessionId);

            ePosList
                    .stream()
                    .filter(s -> s.getProcessing().equals(Processing.HUMO))
                    .forEach(e -> {
                                log.info("Starting reconciliation for terminal: {} in branch: {}", e.getTerminalId(), e.getBranch());

                                var entity = entityBuilder(e, sessionId);

                                var activeRequest = recoActiveRequestBuilder(e, entity.getId());
                                entity.setRefNum(activeRequest.getRefNumber());

                                var activeResponse = huMoGatewayService.reconciliationActive(activeRequest);

                                if (!activeResponse.getStatus() || activeResponse.getData().getError() != null) {
                                    log.info("Terminal: {} {} active reconciliation failed", e.getBranch(), e.getTerminalId());

                                    entity.setResponse(activeResponse.toMap());
                                    entity.setStatus(ResponseStatus.FAILED);
                                    reconciliationDAO.save(entity);
                                    return;
                                }

                                var reconciliationPaymentId = activeResponse.getData().getPaymentID();

                                log.info("For terminalId: {} reco paymentId: {}", e.getTerminalId(), reconciliationPaymentId);

                                var confirmRequest = recoConfirmRequestBuilder(reconciliationPaymentId);

                                var confirmResponse = huMoGatewayService.reconciliationConfirm(confirmRequest);

                                if (!confirmResponse.getStatus() || confirmResponse.getData().getError() != null) {
                                    log.info("Terminal: {} {} confirm reconciliation failed", e.getBranch(), e.getTerminalId());

                                    entity.setResponse(confirmResponse.toMap());
                                    entity.setPaymentId(reconciliationPaymentId);
                                    entity.setStatus(ResponseStatus.FAILED);
                                    reconciliationDAO.save(entity);
                                    return;
                                }

                                saveResult(confirmResponse, entity, reconciliationPaymentId);
                                log.info("TerminalId: {} is reconciled successfully", e.getTerminalId());
                            }
                    );


        }

        log.info("Finished  huMo terminals reconciliation");
    }

    private void saveResult(SimpleResponse<HuMoRecoConfirmResponse> confirmResponse, Reconciliation entity, String reconciliationPaymentId) {
        var confirmResult = confirmResponse.getData();


        entity.setPaymentId(reconciliationPaymentId);
        entity.setResponse(confirmResponse.toMap());
        entity.setCreditsReversalNumber(confirmResult.getCreditsReversalNumber());
        entity.setCreditsReversalAmount(confirmResult.getCreditsReversalAmount());
        entity.setDebitsNumber(confirmResult.getDebitsNumber());
        entity.setDebitsAmount(confirmResult.getDebitsAmount());
        entity.setStatus(ResponseStatus.SUCCESS);
        reconciliationDAO.save(entity);
    }

    private Reconciliation entityBuilder(EPos e, UUID session) {


        var preparedEntity = new Reconciliation(
                e.getTerminalId(),
                ResponseStatus.CREATED,
                session
        );

        return reconciliationDAO.save(preparedEntity);
    }

    private static HuMoRecoConfirmRequest recoConfirmRequestBuilder(String reconciliationPaymentId) {
        return new HuMoRecoConfirmRequest(
                SystemTool.HUMO_LOGIN,
                reconciliationPaymentId,
                SystemTool.HUMO_GATE_SUB_SYSTEM
        );
    }

    private HuMoRecoActiveRequest recoActiveRequestBuilder(EPos e, Long id) {
        var externalId = property.getReco().formatted(id.toString());
        log.info("Prepared externalId for request: {}", externalId);

        return new HuMoRecoActiveRequest(
                SystemTool.HUMO_LOGIN,
                externalId,
                e.getTerminalId(),
                SystemTool.HUMO_GATE_SUB_SYSTEM
        );
    }
}
