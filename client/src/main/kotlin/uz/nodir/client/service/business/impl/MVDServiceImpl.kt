package uz.nodir.client.service.business.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.client.model.dto.mvd.response.MVDAddressResponseDTO
import uz.nodir.client.service.business.MVDService
import uz.nodir.client.service.web.gateway.MVDClientService
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.enums.ResultState


@Service
class MVDServiceImpl(
    private val mvdClientService: MVDClientService
) : MVDService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun address(personCode: String): ResultData<MVDAddressResponseDTO> {
        log.info("Received command to get client address by personCode: $personCode")

        val mvdResult = mvdClientService.clientAddress(personCode)

        if (mvdResult.error != null) {
            log.error("Method finished. Cause: ${mvdResult.error.message}")
            return ResultData(mvdResult.error, false)
        }

        if (mvdResult.data.answerId != 1) {
            log.error("Method finished. Cause: ${mvdResult.data.answerMessage} ")
            return ResultData(
                ResultState.NotProcessed,
                "${mvdResult.data.answerMessage} ${mvdResult.data.answerComment}"
            )
        }

        val response = ResultData.ok(
            MVDAddressResponseDTO(mvdResult.data)
        )

        log.info("Finished command to get client address with response dto: $response")
        return response
    }
}