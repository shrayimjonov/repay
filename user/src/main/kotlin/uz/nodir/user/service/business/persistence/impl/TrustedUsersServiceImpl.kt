package uz.nodir.user.service.business.persistence.impl

import jakarta.validation.constraints.NotNull
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import uz.nodir.repayautoconfigure.utils.SecurityUtils
import uz.nodir.user.model.dto.trustedUser.request.TrustedUserFilterRequestDTO
import uz.nodir.user.model.dto.trustedUser.request.TrustedUserRequestDTO
import uz.nodir.user.model.dto.trustedUser.response.TrustedUserResponseDTO
import uz.nodir.user.model.entity.TrustedUser
import uz.nodir.user.model.entity.TrustedUser_


import uz.nodir.user.service.business.persistence.TrustedUsersService
import uz.nodir.user.service.core.dao.TrustedUserDAO
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.dto.response.core.result.PageResponse
import uz.repay.common.model.enums.ResponseStatus
import uz.repay.common.model.enums.ResultState
import uz.repay.common.service.JpaSpecificationBuilder
import uz.repay.common.utils.DateUtils
import uz.repay.common.utils.PageUtils


@Service
class TrustedUsersServiceImpl(
    private val dao: TrustedUserDAO
) : TrustedUsersService, JpaSpecificationBuilder<TrustedUser>() {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun getTrustedUsers(@NotNull requestDTO: TrustedUserFilterRequestDTO): ResultData<PageResponse<TrustedUserResponseDTO>> {
        log.info("Received command to get trusted users with request dto: $requestDTO")


        val specificationResult = specificationBuilder(requestDTO.params)

        if (!specificationResult.status) {
            log.error("Method finished. Cause: ${specificationResult.error.message}")
            return ResultData(specificationResult.error, false)
        }

        val pageable = PageUtils.pageableBuilder(requestDTO)
        val userResult = dao.findByUsername(specificationResult.data, pageable);


        val resultDTO = ResultData.ok(
            PageUtils.map(
                userResult,
                TrustedUserResponseDTO::from
            )
        )

        log.info("Finished command to get trusted users with response size: ${resultDTO.result.size}")
        return resultDTO
    }

    private fun specificationBuilder(requestDTO: TrustedUserRequestDTO): SimpleResponse<Specification<TrustedUser>> {
        var specification: Specification<TrustedUser> = Specification.where(null)

        val userName = requestDTO.userName ?: run {
            val message = "User name is required!"
            log.error("Specification creation failed. Cause: $message")
            return SimpleResponse(ResultState.NotProcessed.code, message)
        }

        specification = specification.and(createSpecByStringEqualCondition(TrustedUser_.USER_NAME, userName))


        requestDTO.ipAddress
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                specification = specification.and(createSpecByLikeCondition(TrustedUser_.IP, it))
            }

        requestDTO.dateRange
            ?.takeIf { it.dateFrom.isNotEmpty() && it.dateTo.isNotEmpty() }
            ?.let {

                DateUtils.validateDate(requestDTO.dateRange)
                specification =
                    specification.and(
                        createSpecByDateBetweenCondition(
                            TrustedUser_.CREATED_TIME,
                            it.dateFrom,
                            it.dateTo
                        )
                    )
            }


        return SimpleResponse(specification)
    }


    override fun deleteTrustedUserById(id: Long): ResultData<CoreError> {
        log.info("Received command to delete trusted user with id: $id")

        val currentUsername = SecurityUtils.getUsernameV2()

        log.info("Trying username is: $currentUsername")

        val trustedUser = dao.findById(id)
            ?: run {
                val message = "Trusted user with ID $id not found"
                log.error("Method finished: Cause: $message")
                return ResultData(
                    ResultState.EntityNotFound.code,
                    message
                )
            }

        if (trustedUser.userName != currentUsername) {
            val message =
                "User $currentUsername is not authorized to delete trusted user owned by ${trustedUser.userName}"
            log.warn(message)
            return ResultData(ResultState.NotProcessed.code, message)
        }

        dao.deleteById(id)

        log.info("Trusted user with ID {} successfully deleted by user {}", id, currentUsername)
        return ResultData.ok(CoreError(0, ResponseStatus.SUCCESS.name))
    }
}