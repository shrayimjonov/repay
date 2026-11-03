package uz.nodir.court.service.web

import uz.nodir.court.model.dto.nciGateway.response.NCIGatewayLoanInfoChambersResponse
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.dto.response.core.SimpleResponse

interface NCIGatewayClientService {

    fun loanInfoChambers(loanExtId: String): SimpleResponse<ResultData<NCIGatewayLoanInfoChambersResponse>>
}