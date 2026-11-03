package uz.nodir.client.service.web.gateway

import uz.nodir.client.model.dto.gateway.response.MVDAddressResponse
import uz.repay.common.model.dto.response.core.SimpleResponse


interface MVDClientService {

    fun clientAddress(personCode: String): SimpleResponse<MVDAddressResponse>
}