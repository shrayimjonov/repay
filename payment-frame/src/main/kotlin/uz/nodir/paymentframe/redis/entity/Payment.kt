package uz.nodir.paymentframe.redis.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

import uz.nodir.paymentframe.model.payment.request.PaymentCreateRequestDTO
import uz.repay.common.utils.DateUtils
import java.time.LocalDateTime
import java.util.*


@RedisHash("payment_frame", timeToLive = 300)
data class Payment(
    @Id
    val id: UUID? = null,

    val amount: Long = 0,

    @Indexed
    val loanExtId: String? = null,

    val pan: String? = null,

    var paid: Boolean = false,

    val createTime: LocalDateTime? = null,

    val paidTime: LocalDateTime? = null,
) {

    companion object {
        fun from(requestDTO: PaymentCreateRequestDTO) =
            Payment(
                id = UUID.randomUUID(),
                amount = requestDTO.amount,
                loanExtId = requestDTO.loanExtId,
                pan = requestDTO.pan,
                paid = false,
                createTime = DateUtils.getDateTimeNow()
            )

    }
}