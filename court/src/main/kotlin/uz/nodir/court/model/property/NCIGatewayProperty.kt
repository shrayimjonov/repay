package uz.nodir.court.model.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import uz.repay.common.model.property.BaseProperty


@Configuration
@ConfigurationProperties("external.nci-gateway")
class NCIGatewayProperty: BaseProperty()