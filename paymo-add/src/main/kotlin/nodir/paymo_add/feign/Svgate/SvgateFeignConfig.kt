package nodir.paymo_add.feign.Svgate

import feign.auth.BasicAuthRequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


class SvgateFeignConfig {
    @Value("\${svgate.login}")
    private val login: String? = null

    @Value("\${svgate.password}")
    private val password: String? = null

    @Bean
    fun basicAuthRequestInterceptor(): BasicAuthRequestInterceptor {
        return BasicAuthRequestInterceptor(login, password)
    }
}