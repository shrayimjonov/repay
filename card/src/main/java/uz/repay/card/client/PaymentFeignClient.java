package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.repay.card.client.configuration.PaymentClientConfiguration;
import uz.repay.common.model.dto.request.backlash.BacklashCreateRequestDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;

@FeignClient(name = "payment", path = "/payment", url = "${external.payment.url}", configuration = PaymentClientConfiguration.class)
public interface PaymentFeignClient {

    @GetMapping("/api/v1/internal/backlash")
    ResultData<Boolean> createBacklash(BacklashCreateRequestDTO requestDTO);

    @PostMapping("/api/v2/internal/forcePay")
    ResultData<PaymentForcePayResponseDTO> forcePay(PayRequestDTO requestDTO);

    @PostMapping("/api/v3/internal/forcePay")
    ResultData<PaymentForcePayResponseDTO> forcePayV3(PayRequestDTO requestDTO, @RequestParam Integer method);
}
