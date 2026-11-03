package uz.nodir.client.service.business.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.client.model.dto.gateway.response.YHXBBPersonCarResponse
import uz.nodir.client.model.dto.mvd.response.MVDAddressResponseDTO
import uz.nodir.client.model.dto.yhxbb.response.YHXBBPersonCarResponseDTO
import uz.nodir.client.service.business.YHXBBService
import uz.nodir.client.service.business.impl.MVDServiceImpl.Companion
import uz.nodir.client.service.web.gateway.YHXBBClientService
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.enums.ResultState


@Service
class YHXBBServiceImpl(
    private val service: YHXBBClientService
) : YHXBBService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun personCars(personCode: String): ResultData<YHXBBPersonCarResponseDTO> {
        log.info("Received command to get client address by personCode: $personCode")

        val mvdResult = service.personCars(personCode)

        if (mvdResult.error != null) {
            log.error("Method finished. Cause: ${mvdResult.error.message}")
            return ResultData(mvdResult.error, false)
        }

        if (mvdResult.data.result != 1) {
            log.error("Method finished. Cause: ${mvdResult.data.comment}")
            return ResultData(ResultState.NotProcessed, "${mvdResult.data.comment}")
        }

        val response = ResultData.ok(
            YHXBBPersonCarResponseDTO.from(mvdResult.data)
        )

        log.info("Finished command to get client address with response dto: $response")
        return response
    }
}