package uz.nodir.court.model.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import uz.repay.common.model.property.BaseProperty


@Configuration
@ConfigurationProperties(prefix = "external.file-service", ignoreUnknownFields = false, ignoreInvalidFields = false)
class FileServiceProperty : BaseProperty()