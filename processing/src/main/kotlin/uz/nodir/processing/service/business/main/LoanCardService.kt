package uz.nodir.processing.service.business.main

import reactor.core.publisher.Mono
import uz.nodir.processing.model.dto.card.response.LoanCardsResult
import uz.repay.common.model.dto.response.core.ResultData

interface LoanCardService {

    fun getLoanCards(loanId: Long?): Mono<ResultData<List<LoanCardsResult>>>



}