package uz.nodir.logprotocol.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uz.nodir.logprotocol.model.domain.dto.card.request.CardCreditEventRequestDTO
import uz.nodir.logprotocol.service.business.event.CardEventService
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.enums.ResponseStatus


@RestController
@RequestMapping("/api/v1/card_event")
class CardEventController(
    private val cardEventService: CardEventService
) {

    @PostMapping("/credit/davr_mobile")
    fun saveCardCreditToBroker(@RequestBody @Valid requestDTO: CardCreditEventRequestDTO): ResponseEntity<ResultData<CoreError>> {
        cardEventService.saveCardEvent(requestDTO)

        return ResponseEntity.ok(
            ResultData.ok(
                CoreError(0, ResponseStatus.SUCCESS.name)
            )
        )
    }
}