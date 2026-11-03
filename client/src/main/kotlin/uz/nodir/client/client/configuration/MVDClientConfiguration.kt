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
import uz.nodir.client.model.property.MVDProperty
import java.util.concurrent.TimeUnit



class MVDClientConfiguration(
    private val property: MVDProperty,
    private val authProperty: GatewayAuthProperty,
    private val restTemplate: RestTemplate

) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }


    @Bean
    fun mvdLoggerLevel(): Logger.Level {
        val level = Logger.Level.FULL
        log.info("Prepared logger level for mvd client: {}", level)
        return level
    }

    @Bean
    fun mvdRequestInterceptor(): RequestInterceptor {
        log.info("Set auth to mvd client request interceptor with login: {}", authProperty.username)
        return GatewayInterceptor(restTemplate, authProperty)
    }

    @Bean
    fun mvdErrorDecoder(): ErrorDecoder {
        val eDecoder = ErrorDecoder.Default()
        log.info("Prepared error decoder for mvd client: {}", eDecoder)
        return eDecoder
    }

    @Bean
    fun mvdOptions(): Request.Options {

        val rOptions = Request.Options(
            property.connectTimeOut, TimeUnit.MILLISECONDS,
            property.readTimeOut, TimeUnit.MILLISECONDS,
            true
        )

        log.info("Prepared request options for mvd client: {}", rOptions)
        return rOptions
    }
}
