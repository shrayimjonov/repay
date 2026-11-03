package uz.repay.autodebit.client.webClient;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uz.repay.autodebit.model.payment.response.PayResponseDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentWebClient {
    private final WebClient.Builder webClientBuilder;

    public Mono<ResultData<PayResponseDTO>> pay(PayRequestDTO requestDTO) {
        return webClientBuilder.build()
                .post()
                .uri("http://payment-service/api/v1/internal/pay") // Eureka name
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<ResultData<List<NCICourseResponse>>> getCourse() {
        return webClientBuilder.build()
                .get()
                .uri("http://payment-service/api/v2/internal/course")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<ResultData<PayResponseDTO>> payV2(PayRequestDTO requestDTO) {
        return webClientBuilder.build()
                .post()
                .uri("http://payment-service/api/v2/internal/pay") // Eureka name
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

}

