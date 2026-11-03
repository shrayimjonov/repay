package uz.nodir.processing.client

import io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS
import io.netty.handler.timeout.ReadTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import uz.nodir.processing.model.dto.uzCard.request.SVGateBaseRequest
import uz.nodir.processing.model.dto.uzCard.request.param.SvGateBalanceParam
import uz.nodir.processing.model.dto.uzCard.response.SVGateBaseResponse
import uz.nodir.processing.model.dto.uzCard.response.result.BalanceResult
import uz.nodir.processing.model.dto.uzCard.response.result.SvGateCardInfoResult
import uz.nodir.processing.model.property.SVGateProperty
import uz.nodir.processing.utils.ErrorUtils
import uz.nodir.processing.utils.SystemTool
import uz.repay.common.model.dto.response.core.SimpleResponse
import java.time.Duration


@Service
class SVGateWebClient(
    @Qualifier("webClientStandard") private val webClientBuilder: () -> WebClient.Builder,
    private val property: SVGateProperty
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
        client = webClientBuilder
            .invoke()
            .filter(basicAuthentication(property.username, property.password))
            .clientConnector(
                ReactorClientHttpConnector(
                    httpClient
                )
            )
            .baseUrl(property.url + "/api/jsonrpc")
            .build()
    }


    fun balanceGet(tokenList: List<String>): Mono<SimpleResponse<SVGateBaseResponse<BalanceResult>>> {
        val request = SVGateBaseRequest(
            id = SystemTool.requestIDGenerator(),
            method = "balance.get",
            params = SvGateBalanceParam(tokenList)
        )

        log.info("Sending request to svGate to balance.get method with dto: {}", request)


        return client.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<SVGateBaseResponse<BalanceResult>>() {})
            .map { response ->
                log.info("Received response from svGate from balance.get method with result: {}", response)
                SimpleResponse(response)
            }
            .onErrorResume { e ->
                ErrorUtils.handleWebClientException(
                    e,
                    "Occurred error while executing svGate balance.get method:"
                ) // Обработка исключений
            }
    }

    fun cardsGet(tokenList: List<String>): Mono<SimpleResponse<SVGateBaseResponse<List<SvGateCardInfoResult>>>> {
        val request = SVGateBaseRequest(
            id = SystemTool.requestIDGenerator(),
            method = "cards.get",
            params = SvGateBalanceParam(tokenList)
        )

        log.info("Sending request to svGate to cards.get method with dto: {}", request)


        return client.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<SVGateBaseResponse<List<SvGateCardInfoResult>>>() {})
            .map { response ->
                log.info("Received response from svGate from cards.get method with result: {}", response)
                SimpleResponse(response)
            }
            .onErrorResume { e ->
                ErrorUtils.handleWebClientException(
                    e,
                    "Occurred error while executing svGate cards.get method:"
                ) // Обработка исключений
            }
    }
}

