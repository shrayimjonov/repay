package uz.nodir.court.model.property

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated


@Validated
@Configuration
@ConfigurationProperties(prefix = "internal", ignoreUnknownFields = false, ignoreInvalidFields = false)
data class InternalProperty(
    @field:NotBlank
    var requestPrefix: String = ""
)