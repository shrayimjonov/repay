package uz.nodir.processing.model.dto.visa.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VisaCardsInfoRequest(
    @JsonProperty("request_id")
    val requestId: String? = null,

    @JsonProperty("card_ids")
    val cardIds: List<String>? = listOf(),
)