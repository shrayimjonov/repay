package uz.nodir.logprotocol.model.domain.dto.card.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull


data class CardCreditEventRequestDTO(
    @NotBlank
    @JsonProperty("card")
    val card: String,

    @NotNull
    @JsonProperty("amount")
    val amount: Double,

    @NotBlank
    @JsonProperty("rrn")
    val rrn: String,
)