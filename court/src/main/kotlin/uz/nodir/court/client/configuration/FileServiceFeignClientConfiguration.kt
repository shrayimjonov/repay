package uz.nodir.court.client.configuration

import feign.Logger
import feign.Request
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import uz.nodir.court.model.property.FileServiceProperty
import java.util.concurrent.TimeUnit

class FileServiceFeignClientConfiguration(
    private val property: FileServiceProperty
) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }


    @Bean
    fun fileServiceLoggerLevel(): Logger.Level {
        val level = Logger.Level.FULL
        log.info("Prepared logger level for fileService client: {}", level)
        return level
    }

    @Bean
    fun fileServiceErrorDecoder(): ErrorDecoder {
        val eDecoder = ErrorDecoder.Default()
        log.info("Prepared error decoder for fileService client: {}", eDecoder)
        return eDecoder
    }

    @Bean
    fun fileServiceOptions(): Request.Options {

        val rOptions = Request.Options(
            property.connectTimeOut, TimeUnit.MILLISECONDS,
            property.readTimeOut, TimeUnit.MILLISECONDS,
            true
        )

        log.info("Prepared request options for fileService client: {}", rOptions)
        return rOptions
    }
}