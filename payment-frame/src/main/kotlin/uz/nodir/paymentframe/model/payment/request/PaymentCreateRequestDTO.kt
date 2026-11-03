package uz.nodir.paymentframe.model.payment.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import uz.repay.common.model.dto.response.annotations.Card
import uz.repay.common.model.dto.response.annotations.LoanId


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PaymentCreateRequestDTO(

    @field:NotNull
    @field:Min(value = 1L)
    val amount: Long = 0,

    @LoanId
    @field:NotBlank
    val loanExtId: String? = null,

    @Card
    @field:NotBlank
    val pan: String? = null,

    @field:NotBlank
    val expiry: String? = null,
) {
}