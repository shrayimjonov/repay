package uz.nodir.court.service.business

import jakarta.validation.constraints.NotNull
import uz.nodir.court.model.dto.court.request.CourtFilterRequestDTO
import uz.nodir.court.model.dto.court.request.CourtUpdateRequestDTO
import uz.nodir.court.model.dto.court.request.CreateLoanCourtRequestDTO
import uz.nodir.court.model.dto.court.response.CourtFilterResponseDTO
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.ResultData

interface NotificationScheduleService {
    fun create(@NotNull requestDTO: CreateLoanCourtRequestDTO): ResultData<CoreError>

    fun filter(@NotNull requestDTO: CourtFilterRequestDTO): ResultData<CourtFilterResponseDTO>

    fun update(@NotNull requestDTO: CourtUpdateRequestDTO): ResultData<CoreError>
    fun delete(@NotNull id: Long): ResultData<CoreError>
}