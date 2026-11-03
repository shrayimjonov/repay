package uz.nodir.client.model.dto.gateway.request

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap


data class GatewayAuthRequest(
    var grantType: String? = null,
    var username: String? = null,
    var password: String? = null
) {


    fun toMap(): MultiValueMap<String, String> {
        val result: MultiValueMap<String, String> = LinkedMultiValueMap()
        result.add("grant_type", grantType)
        result.add("username", username)
        result.add("password", password)
        return result
    }


}
