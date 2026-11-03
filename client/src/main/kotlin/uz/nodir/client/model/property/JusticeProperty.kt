package uz.nodir.client.model.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import uz.repay.common.model.property.BaseProperty



@Configuration
@ConfigurationProperties(prefix = "external.gateway.justice", ignoreUnknownFields = false, ignoreInvalidFields = false)
class JusticeProperty : BaseProperty()
