package uz.nodir.court.model.dto.court.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull
import uz.nodir.court.model.dto.court.request.param.CourtBaseParam

@Schema(allOf = [CourtBaseParam::class])
class CourtUpdateRequestDTO(
    @JsonProperty("id")
    @field:NotNull
    @field:Min(1)
    @Schema(name = "id", example = "7658", description = "id of court", requiredMode = Schema.RequiredMode.REQUIRED)
    val id: Long = 0L,

    @JsonProperty("sent_date_time")
    override val sentDateTime: String,

    @JsonProperty("message")
    override val message: String,

    @JsonProperty("phone")
    override val phone: String,

    ) : CourtBaseParam(
    sentDateTime,
    message,
    phone
)