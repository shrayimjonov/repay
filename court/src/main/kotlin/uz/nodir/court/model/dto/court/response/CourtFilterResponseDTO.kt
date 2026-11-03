package uz.nodir.court.model.dto.court.response

import uz.nodir.court.model.dto.court.response.result.CourtBaseResult
import uz.repay.common.model.dto.response.core.result.PageResult


class CourtFilterResponseDTO : PageResult() {
    var courts: List<CourtBaseResult> = emptyList()


}