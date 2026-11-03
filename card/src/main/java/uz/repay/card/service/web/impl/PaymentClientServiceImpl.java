package uz.repay.card.service.web.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.PaymentFeignClient;
import uz.repay.card.service.web.PaymentClientService;
import uz.repay.common.model.dto.request.backlash.BacklashCreateRequestDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentClientServiceImpl implements PaymentClientService {
    private final PaymentFeignClient client;

    @Override
    public SimpleResponse<ResultData<Boolean>> createBacklash(BacklashCreateRequestDTO requestDTO) {
        log.info("Sending create backlash request: {}", requestDTO);
        try {
            var response = client.createBacklash(requestDTO);
            log.info("Create backlash response: {}", response);
            return new SimpleResponse<>(response);
        } catch (Exception e) {
            log.error("Create backlash has error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<PaymentForcePayResponseDTO>> forcePay(PayRequestDTO requestDTO) {
        log.info("Sending force pay request: {}", requestDTO);
        try {
            var response = client.forcePay(requestDTO);
            log.info("Received force pay response: {}", response);
            return new SimpleResponse<>(response);
        } catch (Exception e) {
            log.error("Force pay method has error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<PaymentForcePayResponseDTO>> forcePayV3(PayRequestDTO requestDTO) {
        log.info("Sending force pay request: {}", requestDTO);
        try {
            var response = client.forcePayV3(requestDTO, 2);
            log.info("Received force pay response: {}", response);
            return new SimpleResponse<>(response);
        } catch (Exception e) {
            log.error("Force pay method has error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }
}
