package uz.nodir.processing.config


import io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.function.Consumer



@Configuration
class WebClientConfiguration {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        const val RESPONSE_TIME_OUT = 30L
        const val READ_TIME_OUT = 30
        const val WRITE_TIME_OUT = 30
        const val CONNECT_TIME_OUT = 30
        val values = mapOf(
            "responseTimeOut" to RESPONSE_TIME_OUT,
            "readTimeOut" to READ_TIME_OUT,
            "writeTimeOut" to WRITE_TIME_OUT,
            "connectTimeOut" to CONNECT_TIME_OUT,
        )
    }

    @Bean
    fun webClientStandard(): () -> WebClient.Builder {
        log.info("Build standard web Client with params: {}", values)

        val httpClient = HttpClient.create()
            .option(CONNECT_TIMEOUT_MILLIS, CONNECT_TIME_OUT)
            .responseTimeout(Duration.ofSeconds(RESPONSE_TIME_OUT)) // Таймаут на чтение ответа
            .doOnConnected { conn ->
                conn
                    .addHandlerLast(ReadTimeoutHandler(READ_TIME_OUT)) // Таймаут чтения
                    .addHandlerLast(WriteTimeoutHandler(WRITE_TIME_OUT)) // Таймаут записи
            }
        return {
            WebClient.builder()
                .filter { request, next -> // Логирование запросов
                    log.info("Target URL: {}", request.url())
                    next.exchange(request)
                }
                .filter { request, next -> // Логирование ответов
                    next.exchange(request)
                        .doOnNext { response ->
                            log.info("Response status: {}", response.statusCode())
                        }
                }
                .clientConnector(ReactorClientHttpConnector(httpClient))
                .defaultHeaders { headers ->
                    headers.contentType = MediaType.APPLICATION_JSON
                    headers.accept = listOf(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                }
        }
    }

}