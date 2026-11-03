package uz.nodir.processing.service.business.bean

import reactor.core.publisher.Mono
import uz.nodir.processing.service.adapter.CardAdapterService
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.Processing



interface CardBeanRegistrar {
    fun getCardInfoV2AdapterByPC(pc: Processing): Mono<SimpleResponse<CardAdapterService>>
}