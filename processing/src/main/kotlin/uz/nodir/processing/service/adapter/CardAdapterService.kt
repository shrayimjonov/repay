package uz.nodir.processing.service.adapter

import jakarta.validation.constraints.NotNull
import reactor.core.publisher.Mono
import uz.nodir.processing.model.dto.adapter.request.CardBalanceFetchRequest
import uz.nodir.processing.model.dto.card.response.LoanCardsResult
import uz.repay.common.model.dto.response.core.SimpleResponse

import uz.repay.common.model.enums.Processing
import uz.repay.common.model.enums.ResultState



interface CardAdapterService {
    fun getCardsBalance(@NotNull cardsList: List<CardBalanceFetchRequest>): Mono<SimpleResponse<List<LoanCardsResult>>>

    fun getPC(): Mono<SimpleResponse<Processing>> {
        return Mono.just(SimpleResponse<Processing>(501, ResultState.NOT_IMPLEMENTED.message))
    }

}