package uz.repay.card.service.web;

import uz.repay.common.model.dto.request.backlash.BacklashCreateRequestDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;

public interface PaymentClientService {
    SimpleResponse<ResultData<Boolean>> createBacklash(BacklashCreateRequestDTO requestDTO);

    SimpleResponse<ResultData<PaymentForcePayResponseDTO>> forcePay(PayRequestDTO requestDTO);

    SimpleResponse<ResultData<PaymentForcePayResponseDTO>> forcePayV3(PayRequestDTO requestDTO);
}
