package uz.repay.autodebit.service.business.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.service.business.KafkaService;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.enums.AddType;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final static String payV2Topic = "payV2";

    @Override
    public void sendPayV2(PayRequestDTO requestDTO) {
        try {
            log.info("sending: {}", requestDTO);
            kafkaTemplate.send(payV2Topic, requestDTO.toString());
        } catch (Exception e) {
            log.info("Kafka sending error: {}", e.getMessage());
        }
    }

}
