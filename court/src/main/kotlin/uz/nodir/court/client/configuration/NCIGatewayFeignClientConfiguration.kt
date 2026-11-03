package uz.nodir.court.client.configuration

import feign.Logger
import feign.Request
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import uz.nodir.court.model.property.NCIGatewayProperty
import java.util.concurrent.TimeUnit

class NCIGatewayFeignClientConfiguration(
    private val property: NCIGatewayProperty
) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }


    @Bean
    fun nciGatewayLoggerLevel(): Logger.Level {
        val level = Logger.Level.FULL
        log.info("Prepared logger level for nciGateway client: {}", level)
        return level
    }

    @Bean
    fun nciGatewayErrorDecoder(): ErrorDecoder {
        val eDecoder = ErrorDecoder.Default()
        log.info("Prepared error decoder for nciGateway client: {}", eDecoder)
        return eDecoder
    }

    @Bean
    fun nciGatewayOptions(): Request.Options {

        val rOptions = Request.Options(
            property.connectTimeOut, TimeUnit.MILLISECONDS,
            property.readTimeOut, TimeUnit.MILLISECONDS,
            true
        )

        log.info("Prepared request options for nciGateway client: {}", rOptions)
        return rOptions
    }
}