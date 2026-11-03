package uz.nodir.paymentframe.model.payment.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import uz.nodir.paymentframe.redis.entity.Payment
import uz.repay.common.utils.CardUtils
import java.time.LocalDateTime
import java.util.*


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PaymentBaseResponseDTO(
    val id: UUID? = null,

    val amount: Long = 0,

    val loanExtId: String? = null,

    val pan: String? = null,

    var paid: Boolean = false,

    val createTime: LocalDateTime? = null,

    val paidTime: LocalDateTime? = null,
) {
    companion object {
        fun from(payment: Payment) =
            PaymentBaseResponseDTO(
                id = payment.id,
                amount = payment.amount,
                loanExtId = payment.loanExtId,
                pan = CardUtils.maskPanDigits(payment.pan),
                createTime = payment.createTime,
                paid = payment.paid,
                paidTime = payment.paidTime
            )
    }
}