package uz.nodir.court.model.dto.application.response

import com.fasterxml.jackson.annotation.JsonProperty



data class ApplicationCheckResponseDTO(
    @JsonProperty("url")
    val url: String,
)