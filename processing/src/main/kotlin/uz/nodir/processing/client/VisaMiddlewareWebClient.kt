package uz.nodir.processing.client

import io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS
import io.netty.handler.timeout.ReadTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import uz.nodir.processing.model.dto.visa.request.VisaCardsInfoRequest
import uz.nodir.processing.model.dto.visa.response.VisaBaseResponse
import uz.nodir.processing.model.dto.visa.response.param.VisaCardInfoResult
import uz.nodir.processing.model.property.VisaMiddlewareProperty
import uz.nodir.processing.utils.ErrorUtils
import uz.nodir.processing.utils.SystemTool
import uz.repay.common.model.dto.response.core.SimpleResponse
import java.time.Duration


@Service
class VisaMiddlewareWebClient(
    @Qualifier("webClientStandard") private val webClientBuilder: () -> WebClient.Builder,
    private val property: VisaMiddlewareProperty
) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    private lateinit var client: WebClient

    init {
        // Настройка HttpClient с тайм-аутами
        val httpClient = HttpClient.create()
            .option(CONNECT_TIMEOUT_MILLIS, property.connectTimeOut.toInt())
            .responseTimeout(Duration.ofSeconds(property.connectTimeOut)) // Таймаут на чтение ответа
            .doOnConnected { conn ->
                conn
                    .addHandlerLast(ReadTimeoutHandler(property.readTimeOut.toInt())) // Таймаут чтения
            }

        // Настройка WebClient
        client = webClientBuilder()
            .clientConnector(
                ReactorClientHttpConnector(
                    httpClient
                )
            )
            .baseUrl(property.url)
            .build()
    }


    fun cardsInfo(panList: List<String>): Mono<SimpleResponse<VisaBaseResponse<List<VisaCardInfoResult>>>> {
        val request = VisaCardsInfoRequest(
            requestId = SystemTool.requestIDGenerator(),
            cardIds = panList
        )

        log.info("Sending request to visa middleware to cardInfo method with dto: {}", request)


        return client.post()
            .uri("/get_list")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<VisaBaseResponse<List<VisaCardInfoResult>>>() {})
            .map { response ->
                log.info("Received response from visa middleware from cardInfo method with result: {}", response)
                SimpleResponse(response)
            }
            .onErrorResume { e ->
                ErrorUtils.handleWebClientException(
                    e,
                    "Occurred error while executing visa middleware cardInfo method:"
                ) // Обработка исключений
            }
    }

}

