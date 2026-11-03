package uz.nodir.user.service.business.persistence

import jakarta.validation.constraints.NotNull
import uz.nodir.user.model.dto.userProtocol.request.UserProtocolFilterRequestDTO
import uz.nodir.user.model.dto.userProtocol.response.UserProtocolResponseDTO
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.dto.response.core.result.PageResponse

interface UserProtocolService {

    fun getUserProtocol(@NotNull requestDTO: UserProtocolFilterRequestDTO): ResultData<PageResponse<UserProtocolResponseDTO>>

}