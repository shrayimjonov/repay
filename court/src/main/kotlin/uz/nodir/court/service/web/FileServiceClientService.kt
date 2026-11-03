package uz.nodir.court.service.web

import org.springframework.web.multipart.MultipartFile
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.dto.response.core.SimpleResponse



interface FileServiceClientService {

    fun saveFiles(multiFiles: List<MultipartFile>, loanExtId: String): SimpleResponse<ResultData<List<Long>>>
}