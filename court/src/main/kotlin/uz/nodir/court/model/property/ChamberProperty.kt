package uz.nodir.court.model.property

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import uz.repay.common.model.property.BaseProperty


@Validated
@Configuration
@ConfigurationProperties(prefix = "external.chambers", ignoreInvalidFields = false, ignoreUnknownFields = false)
data class ChamberProperty(
    @field:NotBlank
    var username: String = "",

    @field:NotBlank
    var password: String = "",
) : BaseProperty()