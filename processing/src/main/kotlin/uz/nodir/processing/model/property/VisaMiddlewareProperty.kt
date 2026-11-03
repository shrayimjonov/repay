package uz.nodir.processing.model.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import uz.repay.common.model.property.BaseProperty


@Validated
@Configuration
@ConfigurationProperties(prefix = "external.visa-middleware", ignoreUnknownFields = false, ignoreInvalidFields = false)
class VisaMiddlewareProperty : BaseProperty()

