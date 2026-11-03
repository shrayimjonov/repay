package uz.repay.autodebit.client.webClient;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uz.repay.autodebit.model.card.request.HumoCardInfoRequestDTO;
import uz.repay.autodebit.model.card.request.LoanCardsRequestDTO;
import uz.repay.autodebit.model.card.response.HumoCardInfoResponseList;
import uz.repay.common.model.dto.response.cardService.EPosResponseDTO;
import uz.repay.common.model.dto.response.core.LoansCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.result.LoanCardsResult;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CardWebClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>>> getLoansCards(LoanCardsRequestDTO requestDTO) {
        return webClientBuilder.build()
                .post()
                .uri("http://card-service/api/v1/internal/loansCards") // Eureka name
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<ResultData<List<EPosResponseDTO>>> getAllEPosses() {
        return webClientBuilder.build()
                .get()
                .uri("http://card-service/api/v1/internal/ePos")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<Boolean> deactivateCard(Long cardId) {
        return webClientBuilder.build()
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("http://card-service/api/v1/internal/deactivateCard/{cardId}")
                        .build(cardId))
                .retrieve()
                .bodyToMono(Boolean.class);
    }


    public Mono<ResultData<List<LoansCardsResponseDTO<LoanCardsResult>>>> getLoansCardsV2(LoanCardsRequestDTO requestDTO) {
        return webClientBuilder.build()
                .post()
                .uri("http://card-service/api/v2/internal/loansCards") // Eureka name
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<ResultData<HumoCardInfoResponseList>> getHumoCardsInfoByLoanExtIds(HumoCardInfoRequestDTO requestDTO) {
        return webClientBuilder.build()
                .post()
                .uri("http://card-service/api/v2/internal/card-info/humo")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
