package uz.repay.card.service.business.component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.paidRequest.request.PaidEventDTO;
import uz.repay.card.mongo.entity.PaidRequest;
import uz.repay.card.service.core.PaidRequestMongoDAO;
import uz.repay.common.utils.DateUtils;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaidRequestEventComponent {
    private final PaidRequestMongoDAO dao;

    @EventListener
    public void loggingPaidRequest(@NonNull PaidEventDTO paidEventDTO) {
        log.info("Received paid request event dto with: {}", paidEventDTO);

        var paidRequest = new PaidRequest(
                UUID.randomUUID(),
                paidEventDTO.getPayer(),
                paidEventDTO.getProcessing(),
                paidEventDTO.getUsername(),
                DateUtils.getDateTimeNow(),
                paidEventDTO.getLoanExtId()
        );

        var entity = dao.save(paidRequest);

        log.info("Finished paid request event with entity: {}", entity);
    }
}
