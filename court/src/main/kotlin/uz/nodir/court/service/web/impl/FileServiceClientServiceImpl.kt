package uz.nodir.court.service.web.impl

import feign.FeignException
import feign.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import uz.nodir.court.client.FileServiceFeignClient
import uz.nodir.court.service.web.FileServiceClientService
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState


@Service
class FileServiceClientServiceImpl(
    private val client: FileServiceFeignClient
) : FileServiceClientService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun saveFiles(multiFiles: List<MultipartFile>, loanExtId: String): SimpleResponse<ResultData<List<Long>>> {
        log.info("Sending request to File-Service \"save files\" method by loanExtId: $loanExtId")
        try {
            val response = client.saveFiles(multiFiles, loanExtId)
            log.info("Taken response from method \"save files\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"save files\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "File-Service TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"save files\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "File-Service THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an INTERNAL error while executing \"save files\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "File-Service INTERNAL ERROR: ${e.message}")
        }
    }


}