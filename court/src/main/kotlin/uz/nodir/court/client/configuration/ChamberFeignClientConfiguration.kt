package uz.nodir.court.client.configuration

import feign.Logger
import feign.Request
import feign.RequestInterceptor
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import uz.nodir.court.client.interceptor.ChamberInterceptor
import uz.nodir.court.model.property.ChamberProperty
import java.util.concurrent.TimeUnit

class ChamberFeignClientConfiguration(
    private val property: ChamberProperty,
    private val restTemplate: RestTemplate
) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }


    @Bean
    fun chamberLoggerLevel(): Logger.Level {
        val level = Logger.Level.FULL
        log.info("Prepared logger level for chamber client: {}", level)
        return level
    }

    @Bean
    fun chamberRequestInterceptor(): RequestInterceptor {
        log.info("Set auth to chamber client request interceptor with login: {}", property.username)
        return ChamberInterceptor(restTemplate, property)
    }

    @Bean
    fun chamberErrorDecoder(): ErrorDecoder {
        val eDecoder = ErrorDecoder.Default()
        log.info("Prepared error decoder for chamber client: {}", eDecoder)
        return eDecoder
    }

    @Bean
    fun chamberOptions(): Request.Options {

        val rOptions = Request.Options(
            property.connectTimeOut, TimeUnit.MILLISECONDS,
            property.readTimeOut, TimeUnit.MILLISECONDS,
            true
        )

        log.info("Prepared request options for chamber client: {}", rOptions)
        return rOptions
    }
}