package uz.repay.autodebit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.repay.autodebit.client.configuration.PaymentServiceFeignConfiguration;
import uz.repay.autodebit.model.payment.response.PayResponseDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;

import java.util.List;

@FeignClient(name = "payment", path = "/payment", configuration = PaymentServiceFeignConfiguration.class)
public interface PaymentFeignClient {
    @PostMapping("/api/v1/internal/pay")
    ResultData<PayResponseDTO> pay(PayRequestDTO requestDTO);

    @PostMapping("/api/v2/internal/pay")
    ResultData<PayResponseDTO> payV2(PayRequestDTO requestDTO);

    @PostMapping("/api/v3/internal/pay")
    ResultData<PayResponseDTO> payV3(PayRequestDTO requestDTO,
                                     @RequestParam("method") Integer method);

    @GetMapping("/api/v2/internal/course")
    ResultData<List<NCICourseResponse>> getCourse();

}
