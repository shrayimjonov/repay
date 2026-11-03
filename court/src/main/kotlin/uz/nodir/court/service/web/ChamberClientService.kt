package uz.nodir.court.service.web

import uz.nodir.court.model.dto.chambers.request.ChamberClaimRequest
import uz.nodir.court.model.dto.chambers.response.*
import uz.repay.common.model.dto.response.core.SimpleResponse

interface ChamberClientService {

    fun newApplication(request: ChamberClaimRequest): SimpleResponse<ChamberClaimResponse>

    fun getClaimApplicationTypes(): SimpleResponse<List<ChamberClaimDefaultTypeResponse>>

    fun getClaimThemes(): SimpleResponse<List<ChamberClaimDefaultTypeResponse>>

    fun getClaimResponsibleTypes(): SimpleResponse<List<ChamberClaimDefaultTypeResponse>>

    fun getCurrencies(): SimpleResponse<List<ChamberClaimCurrencyTypeResponse>>

    fun getOrganizations(): SimpleResponse<List<ChamberClaimDefaultTypeResponse>>

    fun getLanguages(): SimpleResponse<List<ChamberClaimLanguageTypeResponse>>

    fun getClaimById(id: String): SimpleResponse<ChamberGetClaimResponse?>
}