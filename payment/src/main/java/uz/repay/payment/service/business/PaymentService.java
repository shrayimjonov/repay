package uz.repay.payment.service.business;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;
import uz.repay.payment.model.dto.core.pay.reqeuest.ReverseRequestDTO;
import uz.repay.payment.model.dto.core.pay.response.PayResponseDTO;

@Deprecated
public interface PaymentService {
    ResultData<PayResponseDTO> pay(@NonNull PayRequestDTO requestDTO);
    ResultData<PayResponseDTO> startPay(PayRequestDTO requestDTO);
    ResultData<PaymentForcePayResponseDTO> forcePay(PayRequestDTO requestDTO);
    ResultData<PayResponseDTO> defaultReverseTransaction(@NonNull ReverseRequestDTO requestDTO);
    ResultData<PayResponseDTO> synchronize(Long id);
    ResultData<PayResponseDTO> reverseCredit(@NonNull ReverseRequestDTO requestDTO);
    ResultData<PayResponseDTO> cancelDebit(@NonNull ReverseRequestDTO requestDTO);
}
