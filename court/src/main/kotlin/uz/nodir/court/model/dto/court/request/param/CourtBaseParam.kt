package uz.nodir.court.model.dto.court.request.param

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import uz.repay.common.model.dto.response.annotations.Phone


abstract class CourtBaseParam(

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    @field:Schema(
        name = "sent_date_time",
        example = "20.12.2024 16:40:22",
        description = "court day",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotBlank open val sentDateTime: String?,


    @field:Schema(
        name = "message",
        example = "Shu anketani sudi bo'ladi shahar sudida soat 10:00da",
        description = "sent text",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotBlank open val message: String?,


    @field:Schema(
        name = "phone",
        example = "998901234567",
        description = "Phone number",
        requiredMode = Schema.RequiredMode.REQUIRED,
        pattern = "\\d{12}"
    )
    @field:Phone open val phone: String?,
)