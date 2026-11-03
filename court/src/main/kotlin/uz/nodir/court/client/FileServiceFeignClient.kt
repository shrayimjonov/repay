package uz.nodir.court.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import uz.nodir.court.client.configuration.FileServiceFeignClientConfiguration
import uz.repay.common.model.dto.response.core.ResultData



@FeignClient(name = "file", path = "/file", configuration = [FileServiceFeignClientConfiguration::class])
interface FileServiceFeignClient {

    @PostMapping("/api/v1/internal/file/save")
    fun saveFiles(
        @RequestPart("files") multiFiles: List<MultipartFile>,
        @RequestPart("loan_ext_id") loanExtId: String,
    ): ResultData<List<Long>>

}