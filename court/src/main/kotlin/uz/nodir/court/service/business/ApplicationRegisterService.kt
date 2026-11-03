package uz.nodir.court.service.business

import jakarta.validation.constraints.NotNull
import uz.nodir.court.model.dto.application.request.ApplicationCreateRequestDTO
import uz.nodir.court.model.dto.application.response.ApplicationDataResponseDTO
import uz.nodir.court.model.dto.application.response.ApplicationLoanInfoResponseDTO
import uz.nodir.court.model.dto.application.response.ApplicationCheckResponseDTO
import uz.nodir.court.model.dto.application.response.ApplicationResponseDTO
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.ResultData

interface ApplicationRegisterService {

    fun takeApplicationTypes(): ResultData<ApplicationDataResponseDTO>

    fun typesCacheEvict(): ResultData<CoreError>

    fun loanInfoChambers(@NotNull loanExtId: String): ResultData<ApplicationLoanInfoResponseDTO>

    fun register(@NotNull requestDTO: ApplicationCreateRequestDTO): ResultData<CoreError>

    fun getClaimById(@NotNull id: String): ResultData<ApplicationCheckResponseDTO>

    fun getLoanApplications(@NotNull loanExtId: String): ResultData<List<ApplicationResponseDTO>>
}