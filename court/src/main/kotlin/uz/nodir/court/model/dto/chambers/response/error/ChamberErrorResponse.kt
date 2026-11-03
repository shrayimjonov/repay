package uz.nodir.court.model.dto.chambers.response.error

import com.fasterxml.jackson.annotation.JsonProperty

data class ChamberErrorResponse(
    @JsonProperty("errors")
    val errors: ChambersErrors? = null,

    @JsonProperty("type")
    val type: String? = null,

    @JsonProperty("title")
    val title: String? = null,

    @JsonProperty("status")
    val status: Int? = null,

    @JsonProperty("traceId")
    val traceId: String? = null,
)

data class ChambersErrors(
    @JsonProperty("Message")
    val message: List<String>? = null,
)