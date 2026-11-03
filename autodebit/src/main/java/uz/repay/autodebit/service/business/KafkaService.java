package uz.repay.autodebit.service.business;

import uz.repay.common.model.dto.request.payment.PayRequestDTO;

public interface KafkaService {

    void sendPayV2(PayRequestDTO requestDTO);

}
