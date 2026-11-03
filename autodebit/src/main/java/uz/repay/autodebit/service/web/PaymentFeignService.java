package uz.repay.autodebit.service.web;

import uz.repay.autodebit.model.payment.response.PayResponseDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;

import java.util.List;

public interface PaymentFeignService {
    ResultData<PayResponseDTO> pay(PayRequestDTO requestDTO);

    ResultData<PayResponseDTO> payV2(PayRequestDTO requestDTO);

    ResultData<PayResponseDTO> payV3(PayRequestDTO requestDTO);

    ResultData<List<NCICourseResponse>> getCourse();

}
