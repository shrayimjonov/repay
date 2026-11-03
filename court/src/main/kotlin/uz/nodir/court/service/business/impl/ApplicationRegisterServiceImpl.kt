package uz.nodir.court.service.business.impl

import jakarta.validation.constraints.NotNull
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import uz.nodir.court.model.dto.application.request.ApplicationCreateRequestDTO
import uz.nodir.court.model.dto.application.response.ApplicationDataResponseDTO
import uz.nodir.court.model.dto.application.response.ApplicationLoanInfoResponseDTO
import uz.nodir.court.model.dto.application.response.ApplicationCheckResponseDTO
import uz.nodir.court.model.dto.application.response.ApplicationResponseDTO
import uz.nodir.court.model.dto.chambers.request.ChamberClaimRequest
import uz.nodir.court.model.entity.ChambersApplication
import uz.nodir.court.model.property.InternalProperty
import uz.nodir.court.service.business.ApplicationRegisterService
import uz.nodir.court.service.core.dao.ChambersApplicationDAO
import uz.nodir.court.service.web.ChamberClientService
import uz.nodir.court.service.web.NCIGatewayClientService
import uz.nodir.court.utils.CurrencyMatcher
import uz.repay.common.exception.GeneralErrorException
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.enums.ResponseStatus
import uz.repay.common.model.enums.ResultState
import java.util.*


@Service
class ApplicationRegisterServiceImpl(
    private val chamberClientService: ChamberClientService,
    private val nciGatewayClientService: NCIGatewayClientService,
    private val chambersApplicationDAO: ChambersApplicationDAO,
    private val internalProperty: InternalProperty
) : ApplicationRegisterService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private const val APPLICATION_TYPES_CACHE = "court_application_types_cache"
    }


    @Cacheable(value = [APPLICATION_TYPES_CACHE], sync = true)
    override fun takeApplicationTypes(): ResultData<ApplicationDataResponseDTO> {
        log.info("Received command to take application types")

        val appTypesResult = chamberClientService.getClaimApplicationTypes()

        if (!appTypesResult.status) {
            log.error("Finished command. Cause: ${appTypesResult.error.message}")
            throw GeneralErrorException(appTypesResult.error.message)
        }

        val themesResult = chamberClientService.getClaimThemes()

        if (!themesResult.status) {
            log.error("Finished command. Cause: ${themesResult.error.message}")
            throw GeneralErrorException(themesResult.error.message)
        }

        val responsibleTypesResult = chamberClientService.getClaimResponsibleTypes()

        if (!responsibleTypesResult.status) {
            log.error("Finished command. Cause: ${responsibleTypesResult.error.message}")
            throw GeneralErrorException(responsibleTypesResult.error.message)
        }

        val organizationResult = chamberClientService.getOrganizations()

        if (!organizationResult.status) {
            log.error("Finished command. Cause: ${organizationResult.error.message}")
            throw GeneralErrorException(organizationResult.error.message)
        }

        val currenciesResult = chamberClientService.getCurrencies()

        if (!currenciesResult.status) {
            log.error("Finished command. Cause: ${currenciesResult.error.message}")
            throw GeneralErrorException(currenciesResult.error.message)
        }


        val resultDTO = ApplicationDataResponseDTO.from(
            appTypesResult.data,
            themesResult.data,
            responsibleTypesResult.data,
            currenciesResult.data,
            organizationResult.data
        )

        val responseDTO = ResultData(resultDTO)

        log.info("Finished command to take application types with response dto: $responseDTO")
        return responseDTO
    }

    @CacheEvict(value = [APPLICATION_TYPES_CACHE], allEntries = true)
    override fun typesCacheEvict(): ResultData<CoreError> {
        log.info("Received command to cache evict for application types")

        val resultData = ResultData(
            CoreError(0, ResponseStatus.SUCCESS.name)
        )

        log.info("Finished command to cache evict for application types")
        return resultData
    }

    override fun loanInfoChambers(@NotNull loanExtId: String): ResultData<ApplicationLoanInfoResponseDTO> {
        log.info("Received command to get loan info chambers by loanExtId: $loanExtId")

        val loanInfoResult = nciGatewayClientService.loanInfoChambers(loanExtId)

        if (!loanInfoResult.status) {
            log.error("Finished command. Cause: ${loanInfoResult.error.message}")
            return ResultData(loanInfoResult.error, false)
        }

        if (!loanInfoResult.data.isSuccess) {
            log.error("Finished command. Cause: ${loanInfoResult.data.error.message}")
            return ResultData(loanInfoResult.data.error, false)
        }


        val loanInfo = loanInfoResult.data.result


        val currencyResult = chamberClientService.getCurrencies()

        if (!currencyResult.status) {
            log.error("Finished command. Cause: ${currencyResult.error.message}")
            return ResultData(currencyResult.error, false)
        }

        val currencyMatchResult = CurrencyMatcher.currencyResolver(loanInfo.currency, currencyResult.data)

        if (!currencyMatchResult.status) {
            log.error("Finished command. Cause: ${currencyMatchResult.error.message}")
            return ResultData(currencyMatchResult.error, false)
        }

        val responseDTO = ResultData(ApplicationLoanInfoResponseDTO.from(loanInfo, currencyMatchResult.data))

        log.info("Finished command to get loan info chambers with response dto: $responseDTO")
        return responseDTO
    }

    override fun register(requestDTO: ApplicationCreateRequestDTO): ResultData<CoreError> {
        log.info("Received command to register new application with request dto: $requestDTO")

        val cha = ChambersApplication.from(requestDTO)
        val application = chambersApplicationDAO.save(cha)


        if (application.id == null) {
            val message = "Can't generate extId! Cause is null"

            application.note = message
            chambersApplicationDAO.save(application)

            log.error("Finished command. Cause: $message")
            return ResultData(ResultState.InvalidRequest, message)
        }

        val extId = generateExtId(application.id!!)


        val request = ChamberClaimRequest.from(requestDTO, extId)

        application.extId = extId
        application.status = ResponseStatus.PENDING
        application.request = request.toMap()
        chambersApplicationDAO.save(application)


        val chambersResult = chamberClientService.newApplication(request)

        if (chambersResult.error != null) {

            if (chambersResult.isTimeout) {
                application.status = ResponseStatus.NEED_CONFIRM
            } else {
                application.status = ResponseStatus.FAILED
            }

            application.note = chambersResult.error.message
            application.response = chambersResult.toMap()
            chambersApplicationDAO.save(application)


            log.info("Finished command. Cause: ${chambersResult.error.message}")
            return ResultData(chambersResult.error, false)
        }

        application.response = chambersResult.toMap()
        application.status = ResponseStatus.SUCCESS
        application.appId = chambersResult.data.id
        chambersApplicationDAO.save(application)

        val responseDTO = ResultData(
            CoreError(0, ResponseStatus.SUCCESS.name)
        )

        log.info("Finished command to register new application with response dto: $responseDTO")
        return responseDTO
    }

    private fun generateExtId(id: UUID): String {
        val extId = String.format(internalProperty.requestPrefix, id.toString())
        log.info("Generated extId: $extId")
        return extId
    }


    override fun getClaimById(@NotNull id: String): ResultData<ApplicationCheckResponseDTO> {
        log.info("Received command to get claim by id: $id")

        val chambersEntityResult = chambersApplicationDAO.findByChamberId(id)

        if (chambersEntityResult == null) {
            val message = "Chambers application with id: $id not found in database!"
            log.error("Finished command. Cause: $message")
            return ResultData(ResultState.EntityNotFound, message)
        }

        if (chambersEntityResult.status != ResponseStatus.SUCCESS) {
            val message = "You can't take $id chambers application. Cause is not success!"
            log.error("Finished command. Cause: $message")
            return ResultData(ResultState.NotProcessed, message)
        }

        val chambersServiceResult = chamberClientService.getClaimById(id)

        if (!chambersServiceResult.status) {
            log.error("Finished command. Cause: ${chambersServiceResult.error.message}")
            return ResultData(chambersServiceResult.error, false)
        }

        val chambersResult = chambersServiceResult.data

        if (chambersResult == null) {
            val message = "Chambers application with id: $id not found!"
            log.error("Finished command. Cause: $message")
            return ResultData(ResultState.DataNotFound, message)
        }

        if (chambersResult.claimApplicationIntegrationFileUrl?.claimApplicationLink == null) {
            val message = "Link not found in response!"
            log.error("Finished command. Cause: $message")
            return ResultData(ResultState.DataNotFound, message)
        }

        val link = chambersResult.claimApplicationIntegrationFileUrl.claimApplicationLink
            .replace("https://my-api.chamber.uz", "https://internal.example.com")

        val responseDTO = ResultData(
            ApplicationCheckResponseDTO(
                url = link
            )
        )

        log.info("Finished command to get claim with response dto: $responseDTO")
        return responseDTO
    }


    override fun getLoanApplications(@NotNull loanExtId: String): ResultData<List<ApplicationResponseDTO>> {
        log.info("Received command to get loan applications by loanExtId: $loanExtId")

        val loanExtApplications = chambersApplicationDAO.findByLoanExtId(loanExtId)

        val convertedDTO = loanExtApplications
            .map(ApplicationResponseDTO.Companion::from)

        val responseDTO = ResultData(convertedDTO)

        log.info("Finished command to get loan applications with response dto: $responseDTO")

        return responseDTO
    }

}