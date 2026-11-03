package uz.repay.autodebit.client.webClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.enums.ResultState;


@Service
@Slf4j
@RequiredArgsConstructor
public class LoanWebClient {
    private final WebClient.Builder webClientBuilder;

    public Flux<DebtLoansResult> getDebtLoans() {
        return webClientBuilder.build()
                .get()
                .uri("http://127.0.0.1:8888/loan/api/v1/internal/debtLoans") // Eureka name
                .retrieve()
                .bodyToFlux(DebtLoansResult.class);
    }

    public Mono<ResultData<LoanInfoResponse>> getLoanBalance(LoanSimpleRequestDTO requestDTO) {
        return webClientBuilder.build()
                .post()
                .uri("http://127.0.0.1:8888/loan/api/v1/internal/balance")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<Boolean> removeFromDebtLoan(Long id) {
        return webClientBuilder.build()
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("http://127.0.0.1:8888/loan/api/v1/internal/debtLoan")
                        .queryParam("id", id)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<ResultData<LoanAdditionalResponseDTO>> getLoanAdditionalInfo(LoanSimpleRequestDTO requestDTO) {
        return webClientBuilder.build()
                .post()
                .uri("http://127.0.0.1:8888/loan/api/v1/internal/additional") // Eureka Service Name
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<ResultData<Boolean>> removeDebtLoanByLoanExtId(String loanExtId) {
        return webClientBuilder.build()
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("http://127.0.0.1:8888/loan/api/v1/internal/debtLoanByLoanExtId")
                        .queryParam("id", loanExtId)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<ResultData<byte[]>> getDebtLoansV2() {
        return webClientBuilder.build()
                .get()
                .uri("http://127.0.0.1:8888/loan/api/v2/internal/debtLoans") // Eureka service name
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultData<byte[]>>() {})
                .onErrorResume(error -> {
                    log.error("Error fetching debt loans: {}", error.getMessage());
                    return Mono.just(new ResultData<>(ResultState.BlockOperation, "Error fetching debt loans"));
                });
    }

}
