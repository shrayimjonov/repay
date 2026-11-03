package uz.nodir.processing.model.property

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import uz.repay.common.model.property.BaseProperty

@Validated
@Configuration
@ConfigurationProperties(prefix = "external.master-gate", ignoreInvalidFields = false, ignoreUnknownFields = false)
class MasterGateProperty(
    @field:NotBlank var username: String = "",
    @field:NotBlank var password: String = "",
) : BaseProperty()