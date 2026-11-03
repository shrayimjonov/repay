package uz.nodir.logprotocol.service.business.event

import uz.nodir.logprotocol.model.domain.dto.card.request.CardCreditEventRequestDTO

interface CardEventService {
    fun saveCardEvent(requestDTO: CardCreditEventRequestDTO)
}