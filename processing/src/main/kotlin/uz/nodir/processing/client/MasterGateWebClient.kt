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
import uz.nodir.processing.model.dto.master.request.MasterBaseRequest
import uz.nodir.processing.model.dto.master.request.param.MasterGetBalanceParam
import uz.nodir.processing.model.dto.master.response.MasterBaseResponse
import uz.nodir.processing.model.dto.master.response.result.MasterGetBalanceResult
import uz.nodir.processing.model.property.MasterGateProperty
import uz.nodir.processing.utils.ErrorUtils
import uz.nodir.processing.utils.SystemTool
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState
import java.time.Duration


@Service
class MasterGateWebClient(
    @Qualifier("webClientStandard") private val webClientBuilder: () -> WebClient.Builder,
    private val property: MasterGateProperty
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
            .filter(basicAuthentication(property.username, property.password))
            .clientConnector(
                ReactorClientHttpConnector(
                    httpClient
                )
            )
            .baseUrl(property.url + "/connect")
            .build()
    }


    fun balance(token: String?): Mono<SimpleResponse<MasterBaseResponse<MasterGetBalanceResult>>> {
        if (token == null) {
            val message = "Token is null"
            log.error(message)
            return Mono.just(
                SimpleResponse(
                    ResultState.InvalidRequest.code, message
                )
            )
        }

        val request = MasterBaseRequest(
            id = SystemTool.requestIDGenerator(),
            params = MasterGetBalanceParam(
                token = token
            )
        )

        log.info("Sending request to master gate to getBalance method with dto: {}", request)


        return client.post()
            .uri("/api/v1/card/balance")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<MasterBaseResponse<MasterGetBalanceResult>>() {})
            .map { response ->
                log.info("Received response from master gate from getBalance method with result: {}", response)
                SimpleResponse(response)
            }
            .onErrorResume { e ->
                ErrorUtils.handleWebClientException(
                    e,
                    "Occurred error while executing master gate getBalance method:"
                ) // Обработка исключений
            }
    }

}

