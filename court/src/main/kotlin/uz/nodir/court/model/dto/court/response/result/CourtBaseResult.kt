package uz.nodir.court.model.dto.court.response.result

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import uz.nodir.court.model.entity.Court
import java.time.LocalDateTime


data class CourtBaseResult(
    @JsonProperty("id")
    @field:Schema(
        name = "id",
        example = "123",
        description = "id of court",
        pattern = "\\d"
    )
    val id: Long,

    @JsonProperty("loan_ext_id")
    @field:Schema(
        name = "loan_ext_id",
        example = "01121158076",
        description = "loan external id",
        pattern = "\\d{9,}"
    )
    val loanExtId: String,

    @JsonProperty("executed_date")
    @field:Schema(
        name = "executed_date",
        example = "2024-12-24T12:55:00",
        description = "job will be started at"
    )
    val executedDate: LocalDateTime,

    @JsonProperty("phone")
    @field:Schema(
        name = "phone",
        example = "998901234567",
        description = "phone number"
    )
    val phone: String,

    @JsonProperty("message")
    @field:Schema(
        name = "message",
        example = "Sudga borish kerak",
        description = "text of sms"
    )
    val message: String,
) {
    constructor(court: Court) : this(
        id = court.id!!,
        loanExtId = court.loanExtID!!,
        executedDate = court.executeDate!!,
        phone = court.phone!!,
        message = court.message!!
    )
}