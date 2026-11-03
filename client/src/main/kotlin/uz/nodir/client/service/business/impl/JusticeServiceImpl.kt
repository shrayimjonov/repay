package uz.nodir.client.service.business.impl

import jakarta.annotation.Nonnull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.client.model.dto.gateway.response.result.JusticeDeathResult
import uz.nodir.client.model.dto.gateway.response.result.JusticeMarriageResult
import uz.nodir.client.model.dto.justice.response.JusticeDeathResponseDTO
import uz.nodir.client.model.dto.justice.response.JusticeMarriageResponseDTO
import uz.nodir.client.service.business.JusticeService
import uz.nodir.client.service.web.gateway.JusticeClientService
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.enums.ResultState


@Service
class JusticeServiceImpl(
    private val justClientService: JusticeClientService,
) : JusticeService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun marriage(@Nonnull personCode: String): ResultData<List<JusticeMarriageResponseDTO>> {
        log.info("Received command to get marriage by person code: $personCode")

        val result = justClientService.marriage(personCode)

        if (!result.status) {
            log.error("Method finished. Cause: ${result.error.message}")
            return ResultData(result.error, false)
        }

        val marriageResult = result.data

        if (marriageResult.resultCode != 1) {
            log.error("Method finished. Cause: ${marriageResult.resultMessage}")
            return ResultData(marriageResult.resultCode, marriageResult.resultMessage)
        }

        if (marriageResult.items.isNullOrEmpty()) {
            val message = "Data is empty or null in EGOV!"
            log.error("Method finished. Cause: $message")
            return ResultData(ResultState.DataNotFound, message)
        }

        val response = ResultData.ok(
            marriageResult.items
                .map(::JusticeMarriageResponseDTO)
        )

        log.info("Finished command to get marriage by person with response dto: $response")
        return response
    }


    override fun death(@Nonnull personCode: String): ResultData<List<JusticeDeathResponseDTO>> {
        log.info("Received command to get death by person code: $personCode")

        val result = justClientService.death(personCode)

        if (!result.status) {
            log.error("Method finished. Cause: ${result.error.message}")
            return ResultData(result.error, false)
        }

        val deathResult = result.data

        if (deathResult.resultCode != 1) {
            log.error("Method finished. Cause: ${deathResult.resultMessage}")
            return ResultData(deathResult.resultCode, deathResult.resultMessage)
        }

        if (deathResult.items.isNullOrEmpty()) {
            val message = "Data is empty or null in EGOV!"
            log.error("Method finished. Cause: $message")
            return ResultData(ResultState.DataNotFound, message)
        }

        val response = ResultData.ok(
            deathResult.items
                .map(::JusticeDeathResponseDTO)
        )

        log.info("Finished command to get death by person with response dto: $response")
        return response
    }
}