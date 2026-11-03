package uz.nodir.processing.model.dto.card.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import uz.nodir.processing.model.dto.huMo.response.HuMoCardBaseResponse

import uz.nodir.processing.model.dto.huMo.response.result.HuMoCardResult
import uz.nodir.processing.model.dto.master.response.result.MasterGetBalanceResult
import uz.nodir.processing.model.dto.uzCard.response.result.BalanceInfoResult
import uz.nodir.processing.model.dto.uzCard.response.result.SvGateCardInfoResult
import uz.nodir.processing.model.dto.visa.response.param.VisaCardInfoResult
import uz.nodir.processing.model.entity.Card


import uz.nodir.processing.model.entity.base.BaseCardEntity

import uz.nodir.processing.utils.ServiceUtils.improvePhone
import uz.nodir.processing.utils.ServiceUtils.isExpiredCard
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.dto.response.core.params.DebitCardParams
import uz.repay.common.model.enums.Currency
import uz.repay.common.model.enums.Payer
import uz.repay.common.model.enums.Processing
import uz.repay.common.utils.CardUtils

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoanCardsResult(
    @JsonProperty("id")
    @Schema(name = "id", example = "864", description = "card id")
    var id: Long? = null,

    @JsonProperty("pan")
    @Schema(name = "pan", example = "561468******7308", description = "masked pan")
    var pan: String? = null,

    @JsonProperty("holder")
    @Schema(name = "holder", example = "TOSHMAT ESHMATOV", description = "card holder")
    var holder: String? = null,

    @JsonProperty("phone")
    @Schema(name = "phone", example = "998901234567", description = "phone attached to card")
    var phone: String? = null,

    @JsonProperty("sms")
    @Schema(name = "sms", example = "false", description = "sms information is available")
    var sms: Boolean? = null,

    @JsonProperty("amount")
    @Schema(name = "amount", example = "364100", description = "card balance")
    var amount: Long? = null,

    @JsonProperty("state")
    @Schema(name = "state", example = "0", description = "card status")
    var state: Int? = null,

    @JsonProperty("stateDefinition")
    @Schema(name = "stateDefinition", example = "FAOL", description = "card status translated description")
    var stateDefinition: String? = null,

    @JsonProperty("payer")
    @Schema(
        name = "payer",
        example = "GUARANTOR",
        description = "what is the relationship of the card holder to the creditor"
    )
    var payer: Payer? = Payer.AUTO,

    @JsonProperty("processing")
    @Schema(name = "processing", example = "DAVR_VISA", description = "card processing")
    var processing: Processing? = null,

    @JsonProperty("debitParams")
    @Schema(
        name = "debitParams", example = ("""{
                "type": "pan",
                "card": "ENCRYPTED_CARD_TOKEN",
                "processing": "HUMO"
            }"""), description = "debit params for sending to payment service"
    )
    var debitParams: DebitCardParams? = null,

    @JsonProperty("ipAddress")
    @Schema(name = "ipAddress", example = "127.0.0.1", description = "card adder employee ip address")
    var ipAddress: String? = null,

    @JsonProperty("currency")
    @Schema(name = "currency", example = "UZS", description = "card currency")
    var currency: Currency? = null,

    @JsonProperty("guarantorId")
    @Schema(name = "guarantorId", example = "59", description = "guarantor id if card taken from guarantor")
    var guarantorId: Long? = null,

    @JsonProperty("is_confirmed")
    @Schema(name = "is_confirmed", example = "true", description = "card is confirmed by holder")
    var isConfirmed: Boolean? = true,
) {


    constructor(id: Long?, result: SimpleResponse<HuMoCardBaseResponse<HuMoCardResult>>) : this() {
        this.id = id

        result.let {
            it.data.result?.let { res ->

                this.amount = res.balance?.availableAmount ?: 0

                res.let { cardRes ->
                    cardRes.card?.let { card ->
                        this.holder = card.nameOnCard
                        this.pan = CardUtils.maskPanDigits(card.primaryAccountNumber)

                        val cardStatus = card.statuses
                            ?.item
                            ?.stream()
                            ?.filter { c -> c.type.equals("card") }
                            ?.findFirst()

                        this.state = cardStatus?.map { item -> Integer.valueOf(item.actionCode) }?.orElse(1101)

                        if (card.pinTryCount.equals("3"))
                            this.state = 1106

                    }

                    cardRes.mb?.let { mb ->
                        this.sms = mb.state.equals("on")
                        this.phone = mb.phone?.let { phone -> improvePhone(phone) }
                    } ?: {
                        this.sms = false
                    }
                }

                if (state == 0)
                    this.debitParams = DebitCardParams(res.card?.primaryAccountNumber, Processing.HUMO)

                this.processing = Processing.HUMO
                this.currency = Currency.UZS

            }
        }
    }

    constructor(s: BaseCardEntity, processingIsEnabled: Boolean) : this() {
        val u = s.card
        this.payer = s.payer
        u?.let {
            this.id = u.id
            this.holder = u.holder
            this.phone = u.phone
            this.sms = false
            this.pan = u.maskedPan
            this.amount = 0L
            this.state = when {
                s.isDeleted == true -> 1105
                isExpiredCard(u.expiry) -> 1104
                s.card?.isActive == false -> 1103
                !processingIsEnabled -> 1101
                s.isConfirmed == false -> {
                    this.isConfirmed = false
                    1108
                }

                else -> 1102
            }
            this.processing = u.processing
            this.currency = u.currency
        }
        this.ipAddress = s.ipAddress
    }


    constructor(s: BaseCardEntity?) : this() {
        s?.let {
            val u = s.card
            this.payer = s.payer
            u?.let {
                this.id = u.id
                this.holder = u.holder
                this.phone = u.phone
                this.sms = false
                this.pan = u.maskedPan
                this.amount = 0L
                if (isExpiredCard(u.expiry)) this.state = 1104
                else this.state = 1103
                this.processing = u.processing
                this.currency = u.currency
            }
        }
    }

    constructor(id: Long?, u: SvGateCardInfoResult, processing: Processing) : this() {
        this.id = id
        this.holder = u.fullName
        this.phone = u.phone
        this.sms = u.sms
        this.pan = if (u.pan?.matches("\\d{16}".toRegex()) == true) CardUtils.maskPanDigits(u.pan) else u.pan
        this.amount = u.balance
        this.state = if (u.pinCount == 3) 1106 else u.status // pinCode block
        this.processing = processing
        if (u.sms == true && u.status == 0 && u.pinCount != 3)
            this.debitParams = DebitCardParams(u.token, processing)
        this.currency = if (CardUtils.isSumPC(processing)) Currency.UZS else Currency.USD
    }

    constructor(id: Long?, u: VisaCardInfoResult, processing: Processing) : this() {
        this.id = id
        this.holder = u.fullName
        this.phone = u.phone
        this.sms = u.sms
        this.pan = if (u.pan?.matches("\\d{16}".toRegex()) == true) CardUtils.maskPanDigits(u.pan) else u.pan
        this.amount = u.balance
        this.state = if (u.pinCount == 3) 1106 else u.status // pinCode block
        this.processing = processing
        if (u.sms == true && u.status == 0 && u.pinCount != 3)
            this.debitParams = DebitCardParams(u.pan, processing)
        this.currency = if (CardUtils.isSumPC(processing)) Currency.UZS else Currency.USD
    }

    constructor(k: Long, u: BalanceInfoResult, card: Card, processing: Processing) : this() {
        this.id = k
        this.holder = card.holder
        this.phone = "998901234567"
        this.sms = true
        this.pan = if (u.pan?.matches("\\d{16}".toRegex()) == true) CardUtils.maskPanDigits(u.pan) else u.pan
        this.amount = u.balance
        this.state = 0 // pinCode block
        this.processing = processing
        this.debitParams = DebitCardParams(u.token, processing)
        this.currency = if (CardUtils.isSumPC(processing)) Currency.UZS else Currency.USD
    }


    constructor(k: Long?, u: MasterGetBalanceResult?) : this() {
        this.id = k
        u?.let {
            this.holder = u.holder
            this.phone = u.phone
            this.sms = true
            this.pan = if (u.pan?.matches("\\d{16}".toRegex()) == true) CardUtils.maskPanDigits(u.pan) else u.pan
            this.amount = u.balance
            this.state = if (u.pinCount == 3) 1106 else u.status // pinCode block
            this.processing = Processing.DAVR_MASTER_CARD
            if (u.status == 0) this.debitParams = DebitCardParams(u.token, processing)
            this.currency = if (CardUtils.isSumPC(processing)) Currency.UZS else Currency.USD
        }
    }
}
