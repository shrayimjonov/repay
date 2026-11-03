package uz.nodir.client.service.web.gateway

import uz.nodir.client.model.dto.gateway.response.JusticeBaseResponse
import uz.nodir.client.model.dto.gateway.response.result.JusticeDeathResult
import uz.nodir.client.model.dto.gateway.response.result.JusticeMarriageResult
import uz.repay.common.model.dto.response.core.SimpleResponse

interface JusticeClientService {

    fun marriage(personCode: String): SimpleResponse<JusticeBaseResponse<List<JusticeMarriageResult>>>

    fun death(personCode: String): SimpleResponse<JusticeBaseResponse<List<JusticeDeathResult>>>

}