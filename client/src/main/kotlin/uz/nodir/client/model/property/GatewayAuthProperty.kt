package uz.nodir.client.model.property

import lombok.AllArgsConstructor
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "external.gateway.auth")
data class GatewayAuthProperty(
    var url: String? = null,
    var username: String? = null,
    var password: String? = null,
    var basicAuthCredentials: GatewayBasicAuthCredentials? = null
)

data class GatewayBasicAuthCredentials(
    var username: String? = null,
    var password: String? = null,
)
