package uz.nodir.court.client.interceptor

import feign.RequestInterceptor
import feign.RequestTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import uz.nodir.court.model.dto.chambers.request.ChamberAuthRequest
import uz.nodir.court.model.dto.chambers.response.ChamberAuthResponse
import uz.nodir.court.model.property.ChamberProperty
import uz.repay.common.exception.GatewayException

class ChamberInterceptor(
    private val restTemplate: RestTemplate,
    private val property: ChamberProperty,
) : RequestInterceptor {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    private var token: ChamberAuthResponse? = null
    private val tokenRequest = ChamberAuthRequest(
        property.username,
        property.password
    )
    private var expireTimeToken: Long = 0

    override fun apply(p0: RequestTemplate) {
        p0.header("Content-Type", "application/json")
        p0.header("Accept", "application/json")
        p0.header("Authorization", "Bearer $accessToken")
    }

    private val accessToken: String
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
            log.debug("Chambers auth token expire time {}, Current TS {}", expireTimeToken, ts)
            return expireTimeToken - 3600000 <= ts // expireTime - 1 hour
        }

    private fun requestNewToken() {
        val url: String = property.url + "/Account/IntegrationLogin"
        log.info("Request new Access token for Chambers auth to URL: {}", url)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity(tokenRequest, headers)

        try {
            token = restTemplate.postForEntity(url, request, ChamberAuthResponse::class.java).body

            log.info("Received response from auth: {}", token)

            /*
             save token until next day.
             Token works 24 hours.
             But I'm used 23 hours
             */

            expireTimeToken = System.currentTimeMillis() + 24 * 60 * 60 * 1000
        } catch (e: Exception) {
            val message =
                "Taken an error while taking token from chambers auth service with message: %s".formatted(e.message)
            log.error(message)
            throw GatewayException(message)
        }
    }
}