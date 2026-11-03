package uz.nodir.client.client.interceptor

import feign.RequestInterceptor
import feign.RequestTemplate
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.support.BasicAuthenticationInterceptor
import org.springframework.web.client.RestTemplate
import uz.nodir.client.model.dto.gateway.request.GatewayAuthRequest
import uz.nodir.client.model.dto.gateway.response.GatewayAuthResponse
import uz.nodir.client.model.property.GatewayAuthProperty
import uz.repay.common.exception.GatewayException

@Slf4j
class GatewayInterceptor(
    private val restTemplate: RestTemplate,
    private val property: GatewayAuthProperty
) : RequestInterceptor {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    private var token: GatewayAuthResponse? = null
    private val tokenRequest = GatewayAuthRequest(
        "password",
        property.username,
        property.password
    )
    private var expireTimeToken: Long = 0

    override fun apply(requestTemplate: RequestTemplate) {
        requestTemplate.header("Content-Type", "application/json")
        requestTemplate.header("Accept", "application/json")
        requestTemplate.header("Authorization", "Bearer $accessToken")
    }

    private val accessToken: String?
        get() {
            if (token == null || isExpired) {
                log.info("Token is null or expired")
                requestNewToken()
            }
            return token!!.accessToken
        }

    private val isExpired: Boolean
        get() {
            val ts = System.currentTimeMillis()
            log.debug("Gateway token expire time {}, Current TS {}", expireTimeToken, ts)
            return expireTimeToken - 3600000 <= ts // expireTime - 1 hour
        }

    private fun requestNewToken() {
        val url: String = property.url + "/api/v1/auth/oauth2/token"
        log.info("Request new Access token for gateway davr to URL: {}", url)
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val request = HttpEntity(tokenRequest.toMap(), headers)
        try {
            restTemplate.interceptors.add(
                BasicAuthenticationInterceptor(
                    property.basicAuthCredentials!!.username!!,
                    property.basicAuthCredentials!!.password!!
                )
            )

            token = restTemplate.postForEntity(url, request, GatewayAuthResponse::class.java).body

            log.info("Received response from auth: {}", token)
            expireTimeToken = System.currentTimeMillis() + token!!.expiresIn!! * 1000L
        } catch (e: Exception) {
            val message =
                "Taken an error while taking token from davr gateway auth service with message: %s".formatted(e.message)
            log.error(message)
            throw GatewayException(message)
        }
    }
}
