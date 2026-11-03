package uz.nodir.user.service.business.persistence.impl

import jakarta.validation.constraints.NotNull
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import uz.nodir.user.model.dto.userProtocol.request.UserProtocolFilterRequestDTO
import uz.nodir.user.model.dto.userProtocol.request.UserProtocolRequestDTO
import uz.nodir.user.model.dto.userProtocol.response.UserProtocolResponseDTO
import uz.nodir.user.model.entity.UserProtocol
import uz.nodir.user.model.entity.UserProtocol_
import uz.nodir.user.service.business.persistence.UserProtocolService
import uz.nodir.user.service.core.dao.UserProtocolDAO
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.dto.response.core.result.PageResponse
import uz.repay.common.model.enums.ResultState
import uz.repay.common.service.JpaSpecificationBuilder
import uz.repay.common.utils.DateUtils
import uz.repay.common.utils.PageUtils


@Service
class UserProtocolServiceImpl(
    private val userProtocolDAO: UserProtocolDAO
) : UserProtocolService, JpaSpecificationBuilder<UserProtocol>() {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun getUserProtocol(@NotNull requestDTO: UserProtocolFilterRequestDTO): ResultData<PageResponse<UserProtocolResponseDTO>> {
        log.info("Received command to get user protocol with request dto: $requestDTO")


        val specificationResult = specificationBuilder(requestDTO.params)

        if (!specificationResult.status) {
            log.error("Method finished. Cause: ${specificationResult.error.message}")
            return ResultData(specificationResult.error, false)
        }

        val pageable = PageUtils.pageableBuilder(requestDTO)
        val userResult = userProtocolDAO.findByUsername(specificationResult.data, pageable);


        val resultDTO = ResultData.ok(
            PageUtils.map(
                userResult,
                UserProtocolResponseDTO::from
            )
        )

        log.info("Finished command to get user protocol with response size: ${resultDTO.result.size}")
        return resultDTO
    }

    private fun specificationBuilder(requestDTO: UserProtocolRequestDTO): SimpleResponse<Specification<UserProtocol>> {
        var specification: Specification<UserProtocol> = Specification.where(null)

        val userName = requestDTO.userName ?: run {
            val message = "User name is required!"
            log.error("Specification creation failed. Cause: $message")
            return SimpleResponse(ResultState.NotProcessed.code, message)
        }

        specification = specification.and(createSpecByStringEqualCondition(UserProtocol_.USER_NAME, userName))

        requestDTO.lastName
            ?.takeIf { it.isNotEmpty() }
            ?.let {
            specification = specification.and(createSpecByStringEqualCondition(UserProtocol_.LAST_NAME, it))
        }

        requestDTO.firstName
            ?.takeIf { it.isNotEmpty() }
            ?.let {
            specification = specification.and(createSpecByStringEqualCondition(UserProtocol_.FIRST_NAME, it))
        }

        requestDTO.ipAddress
            ?.takeIf { it.isNotEmpty() }
            ?.let {
            specification = specification.and(createSpecByLikeCondition(UserProtocol_.IP_ADDRESS, it))
        }

        requestDTO.dateRange
            ?.takeIf { it.dateTo.isNotEmpty() && it.dateFrom.isNotEmpty() }
            ?.let {

            DateUtils.validateDate(requestDTO.dateRange)
            specification =
                specification.and(createSpecByDateBetweenCondition(UserProtocol_.CREATED_TIME, it.dateFrom, it.dateTo))
        }


        return SimpleResponse(specification)
    }
}