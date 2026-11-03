package uz.nodir.user.service.business.persistence

import jakarta.validation.constraints.NotNull
import uz.nodir.user.model.dto.trustedUser.request.TrustedUserFilterRequestDTO
import uz.nodir.user.model.dto.trustedUser.response.TrustedUserResponseDTO
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.dto.response.core.result.PageResponse

interface TrustedUsersService {

    fun getTrustedUsers(@NotNull requestDTO: TrustedUserFilterRequestDTO): ResultData<PageResponse<TrustedUserResponseDTO>>

    fun deleteTrustedUserById(@NotNull id: Long): ResultData<CoreError>
}