package uz.repay.payment.service.business;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;
import uz.repay.payment.model.dto.core.pay.reqeuest.ReverseRequestDTO;
import uz.repay.payment.model.dto.core.pay.response.PayResponseDTO;

public interface PaymentServiceV2 {
    ResultData<PayResponseDTO> payV2(@NonNull PayRequestDTO requestDTO);

    ResultData<PayResponseDTO> payV3(@NonNull PayRequestDTO requestDTO, Integer method);

    ResultData<PayResponseDTO> defaultReverseTransaction(@NonNull ReverseRequestDTO requestDTO);

    ResultData<PayResponseDTO> synchronize(@NonNull Long tranId);

    ResultData<PayResponseDTO> reverseCredit(@NonNull ReverseRequestDTO requestDTO);

    ResultData<PayResponseDTO> cancelDebit(@NonNull ReverseRequestDTO requestDTO);

    ResultData<PayResponseDTO> completeTransaction(@NotNull Long tranId);

    ResultData<PaymentForcePayResponseDTO> forcePay(@NonNull PayRequestDTO requestDTO);

    ResultData<PaymentForcePayResponseDTO> forcePayV3(@NonNull PayRequestDTO requestDTO, Integer method);
}
