package uz.nodir.client.service.business

import jakarta.annotation.Nonnull
import uz.nodir.client.model.dto.justice.response.JusticeDeathResponseDTO
import uz.nodir.client.model.dto.justice.response.JusticeMarriageResponseDTO
import uz.repay.common.model.dto.response.core.ResultData

interface JusticeService {

    fun marriage(@Nonnull personCode: String): ResultData<List<JusticeMarriageResponseDTO>>

    fun death(@Nonnull personCode: String): ResultData<List<JusticeDeathResponseDTO>>
}