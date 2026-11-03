package uz.nodir.client.client.configuration


import feign.Logger
import feign.Request
import feign.RequestInterceptor
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import uz.nodir.client.client.interceptor.GatewayInterceptor
import uz.nodir.client.model.property.GatewayAuthProperty
import uz.nodir.client.model.property.YHXBBProperty
import java.util.concurrent.TimeUnit



class YHXBBClientConfiguration(
    private val property: YHXBBProperty,
    private val authProperty: GatewayAuthProperty,
    private val restTemplate: RestTemplate

) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }


    @Bean
    fun yhxbbLoggerLevel(): Logger.Level {
        val level = Logger.Level.FULL
        log.info("Prepared logger level for yhxbb client: {}", level)
        return level
    }

    @Bean
    fun yhxbbRequestInterceptor(): RequestInterceptor {
        log.info("Set auth to yhxbb client request interceptor with login: {}", authProperty.username)
        return GatewayInterceptor(restTemplate, authProperty)
    }

    @Bean
    fun yhxbbErrorDecoder(): ErrorDecoder {
        val eDecoder = ErrorDecoder.Default()
        log.info("Prepared error decoder for yhxbb client: {}", eDecoder)
        return eDecoder
    }

    @Bean
    fun yhxbbOptions(): Request.Options {

        val rOptions = Request.Options(
            property.connectTimeOut, TimeUnit.MILLISECONDS,
            property.readTimeOut, TimeUnit.MILLISECONDS,
            true
        )

        log.info("Prepared request options for yhxbb client: {}", rOptions)
        return rOptions
    }
}
