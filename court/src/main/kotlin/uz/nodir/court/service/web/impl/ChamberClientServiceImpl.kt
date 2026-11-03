package uz.nodir.court.service.web.impl

import feign.FeignException
import feign.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.court.client.ChamberFeignClient
import uz.nodir.court.model.dto.chambers.request.ChamberClaimRequest
import uz.nodir.court.model.dto.chambers.response.*
import uz.nodir.court.service.web.ChamberClientService
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState


@Service
class ChamberClientServiceImpl(
    private val client: ChamberFeignClient
) : ChamberClientService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun newApplication(request: ChamberClaimRequest): SimpleResponse<ChamberClaimResponse> {
        log.info("Sending request to Chambers \"new application\" method with request: {}", request)
        try {
            val response = client.postClaimApplication(request)
            log.info("Taken response from method \"new application\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"new application\" method with message: {}", r.message)
            return SimpleResponse("Chambers TIME OUT: ${r.message}", false)
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"new application\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Chambers THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"new application\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Chambers INTERNAL ERROR: ${e.message}")
        }
    }


    override fun getClaimApplicationTypes(): SimpleResponse<List<ChamberClaimDefaultTypeResponse>> {
        log.info("Sending request to Chambers \"get claim app types\" method")
        try {
            val response = client.getClaimApplicationTypes()
            log.info("Taken response from method \"get claim app types\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"get claim app types\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Chambers TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"get claim app types\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Chambers THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"get claim app types\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Chambers INTERNAL ERROR: ${e.message}")
        }
    }

    override fun getClaimThemes(): SimpleResponse<List<ChamberClaimDefaultTypeResponse>> {
        log.info("Sending request to Chambers \"get claim themes\" method")
        try {
            val response = client.getClaimThemes()
            log.info("Taken response from method \"get claim themes\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"get claim themes\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Chambers TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"get claim themes\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Chambers THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"get claim themes\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Chambers INTERNAL ERROR: ${e.message}")
        }
    }


    override fun getClaimResponsibleTypes(): SimpleResponse<List<ChamberClaimDefaultTypeResponse>> {
        log.info("Sending request to Chambers \"get claim responsible types\" method")
        try {
            val response = client.getClaimResponsibleTypes()
            log.info("Taken response from method \"get claim responsible types\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"get claim responsible types\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Chambers TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"get claim responsible types\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Chambers THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"get claim responsible types\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Chambers INTERNAL ERROR: ${e.message}")
        }
    }


    override fun getCurrencies(): SimpleResponse<List<ChamberClaimCurrencyTypeResponse>> {
        log.info("Sending request to Chambers \"get currencies\" method")
        try {
            val response = client.getCurrencies()
            log.info("Taken response from method \"get currencies\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"get currencies\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Chambers TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"get currencies\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Chambers THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"get currencies\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Chambers INTERNAL ERROR: ${e.message}")
        }
    }

    override fun getOrganizations(): SimpleResponse<List<ChamberClaimDefaultTypeResponse>> {
        log.info("Sending request to Chambers \"get organizations\" method")
        try {
            val response = client.getOrganizations()
            log.info("Taken response from method \"get organizations\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"get organizations\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Chambers TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"get organizations\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Chambers THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"get organizations\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Chambers INTERNAL ERROR: ${e.message}")
        }
    }

    override fun getLanguages(): SimpleResponse<List<ChamberClaimLanguageTypeResponse>> {
        log.info("Sending request to Chambers \"get languages\" method")
        try {
            val response = client.getLanguages()
            log.info("Taken response from method \"get languages\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"get languages\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Chambers TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"get languages\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Chambers THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"get languages\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Chambers INTERNAL ERROR: ${e.message}")
        }
    }

    override fun getClaimById(id: String): SimpleResponse<ChamberGetClaimResponse?> {
        log.info("Sending request to Chambers \"get claim by id\" method")
        try {
            val response = client.getClaimById(id)
            log.info("Taken response from method \"get claim by id\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"get claim by id\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Chambers TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"get claim by id\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Chambers THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"get claim by id\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Chambers INTERNAL ERROR: ${e.message}")
        }
    }



}