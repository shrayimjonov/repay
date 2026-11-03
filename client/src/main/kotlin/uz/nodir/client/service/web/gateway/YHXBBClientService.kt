package uz.nodir.client.service.web.gateway

import uz.nodir.client.model.dto.gateway.response.YHXBBPersonCarResponse
import uz.repay.common.model.dto.response.core.SimpleResponse

interface YHXBBClientService {

    fun personCars(personCode: String): SimpleResponse<YHXBBPersonCarResponse>
}