package uz.repay.loan.service.business.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.config.kafka.Topics;
import uz.repay.common.model.dto.request.kafka.LogProtocolEventDTO;
import uz.repay.common.model.enums.ProtocolType;
import uz.repay.common.utils.HttpServletUtils;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class LogProtocolComponent {
    private final KafkaProducerComponent kafkaProducerComponent;
    private final ObjectMapper mapper = new ObjectMapper();

    public void saveLog(String loanExtId, ProtocolType protocolType, Map<Object, Object> request) {
        log.info("Received log for saving: {}  {} {}", loanExtId, protocolType, request);

        var eventDTO = new LogProtocolEventDTO(
                loanExtId,
                protocolType,
                request,
                HttpServletUtils.getRequestIPAddress(),
                SecurityUtils.getUsername()
        );


        try {
            var result = kafkaProducerComponent.send(Topics.LOG_PROTOCOL, mapper.writeValueAsString(eventDTO));
            log.info("Save log result: {}", result);
        } catch (JsonProcessingException e) {
            log.error("Save log protocol has error: {}", e.getMessage());
        }

    }
}
