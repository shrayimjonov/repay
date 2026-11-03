package uz.nodir.client.service.business

import jakarta.annotation.Nonnull
import uz.nodir.client.model.dto.mvd.response.MVDAddressResponseDTO
import uz.repay.common.model.dto.response.core.ResultData

interface MVDService {

    fun address(@Nonnull personCode: String): ResultData<MVDAddressResponseDTO>

}