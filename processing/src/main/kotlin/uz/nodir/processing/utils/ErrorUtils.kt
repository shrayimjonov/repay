package uz.nodir.processing.utils

import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState

class ErrorUtils {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        @JvmStatic
        fun <T> handleWebClientException(e: Throwable, message: String): Mono<SimpleResponse<T>> {
            return when (e) {
                is WebClientRequestException -> {
                    // Обрабатываем ошибки, связанные с таймаутом или сетью
                    log.error("$message {}", "TIME OUT ${e.message}")
                    Mono.just(SimpleResponse(e.message, true))
                }

                is WebClientResponseException -> {
                    // Обрабатываем HTTP ошибки (например, 4xx или 5xx)
                    log.error("$message {}", "FEIGN ERROR ${e.message}")
                    Mono.just(SimpleResponse(ResultState.ThirdPartyError.code, e.message))
                }

                else -> {
                    // Обрабатываем другие ошибки
                    log.error("$message {}", "Back ERROR ${e.message}")
                    Mono.just(
                        SimpleResponse<T>(ResultState.InternalServerError.code, e.message)
                    )
                }
            }
        }
    }
}