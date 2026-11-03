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
import uz.nodir.processing.model.dto.huMo.request.HuMoMiddlewareBaseRequest
import uz.nodir.processing.model.dto.huMo.request.param.HuMoCardV3InfoRequestParam
import uz.nodir.processing.model.dto.huMo.response.HuMoCardBaseResponse
import uz.nodir.processing.model.dto.huMo.response.result.HuMoCardResult
import uz.nodir.processing.model.property.HuMoMiddlewareProperty
import uz.nodir.processing.utils.ErrorUtils
import uz.nodir.processing.utils.SystemTool
import uz.repay.common.exception.GeneralErrorException
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState
import java.time.Duration


@Service
class HuMoMiddlewareWebClient(
    @Qualifier("webClientStandard") private val webClientBuilder: () -> WebClient.Builder,
    private val property: HuMoMiddlewareProperty
) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        const val AUTHORIZATION_HEADER = "Authorization"
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
        client = webClientBuilder
            .invoke()
            .defaultHeaders { headers -> headers.set(AUTHORIZATION_HEADER, "Bearer ${property.token}") }
            .clientConnector(
                ReactorClientHttpConnector(
                    httpClient
                )
            )
            .baseUrl(property.url)
            .build()
    }


    fun cardInfo(pan: String?): Mono<SimpleResponse<HuMoCardBaseResponse<HuMoCardResult>>> {

        if (pan == null) {
            val message = "Pan is null"
            log.error(message)
            return Mono.just(
                SimpleResponse(
                    ResultState.InvalidRequest.code, message
                )
            )
        }

        val request = HuMoMiddlewareBaseRequest(
            id = SystemTool.requestIDGenerator(),
            params = HuMoCardV3InfoRequestParam(
                primaryAccountNumber = pan
            )
        )

        log.info("Sending request to huMo middleware to card info v3 method with dto: {}", request.toString())


        return client.post()
            .uri("/v3/iiacs/card")
            .bodyValue(request)
            .retrieve()
            .onStatus({ it.is4xxClientError }) { clientResponse ->
                // Парсим тело ответа в объект
                clientResponse
                    .bodyToMono(object : ParameterizedTypeReference<HuMoCardBaseResponse<Any>>() {})
                    .flatMap { errorResult -> Mono.error(GeneralErrorException("Client error: ${errorResult.error?.message}")) }
            }
            .bodyToMono(object : ParameterizedTypeReference<HuMoCardBaseResponse<HuMoCardResult>>() {})
            .map { response ->
                log.info("Received response from huMo middleware from card info v3 method with result: {}", response)
                SimpleResponse(response)
            }
            .onErrorResume { e ->
                ErrorUtils.handleWebClientException(
                    e,
                    "Occurred error while executing huMo middleware card info v3 method:"
                ) // Обработка исключений
            }
    }
}

