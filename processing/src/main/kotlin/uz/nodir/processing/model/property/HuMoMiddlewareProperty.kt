package uz.nodir.processing.model.property

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import uz.repay.common.model.property.BaseProperty


@Validated
@Configuration
@ConfigurationProperties(prefix = "external.humo", ignoreInvalidFields = false, ignoreUnknownFields = false)
data class HuMoMiddlewareProperty(
    @field:NotBlank
    var token: String = ""
) : BaseProperty()