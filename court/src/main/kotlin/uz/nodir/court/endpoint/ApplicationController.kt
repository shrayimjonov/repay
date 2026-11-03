package uz.nodir.court.endpoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import uz.nodir.court.model.dto.application.request.ApplicationCreateRequestDTO
import uz.nodir.court.service.business.ApplicationRegisterService
import uz.repay.common.model.dto.response.annotations.LoanId


@RestController
@RequestMapping(
    "/api/v1/application",
    produces = [MediaType.APPLICATION_JSON_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE]
)
@Tag(name = "court-application-endpoint")
@PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_COURT_SHOW_APPLICATION)")
class ApplicationController(
    private val applicationRegisterService: ApplicationRegisterService
) {

    @GetMapping("/types")
    @Operation(
        summary = "Application types",
        description = "Take chambers application types for filling fields",
        operationId = "court-application-chambers-types"
    )
    fun getTypes() =
        ResponseEntity.ok(applicationRegisterService.takeApplicationTypes())

    @RequestMapping(method = [RequestMethod.OPTIONS], value = ["/cacheEvict"])
    @Operation(
        summary = "Application cache eviction",
        description = "Cache evict for chambers application types",
        operationId = "court-application-chambers-types-eviction"
    )
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    fun cacheEvict() =
        ResponseEntity.ok(applicationRegisterService.typesCacheEvict())

    @GetMapping("/loan/info")
    @Operation(
        summary = "Loan Info",
        description = "Get loan info for filling fields",
        operationId = "court-application-chambers-loan-info"
    )
    fun loanInfo(@RequestParam("loan_ext_id") @Valid @LoanId loanExtId: String) =
        ResponseEntity.ok(applicationRegisterService.loanInfoChambers(loanExtId))

    @PostMapping
    @Operation(
        summary = "Application registration",
        description = "Create new application in chambers service",
        operationId = "court-application-chambers-register"
    )
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_COURT_REGISTER_APPLICATION)")
    fun register(@RequestBody @Valid request: ApplicationCreateRequestDTO) =
        ResponseEntity.ok(applicationRegisterService.register(request))

    @GetMapping("/check")
    @Operation(
        summary = "Application check",
        description = "Check application status from chambers service",
        operationId = "court-application-chambers-check"
    )
    fun checkApplicationById(@RequestParam("id") id: String) =
        ResponseEntity.ok(applicationRegisterService.getClaimById(id))

    @GetMapping("/byLoan")
    @Operation(
        summary = "Application get by loan",
        description = "Get all loan applications",
        operationId = "court-application-by-loan"
    )  fun getApplicationsByLoan(@RequestParam("loan_ext_id") @Valid @LoanId loanExtId: String) =
        ResponseEntity.ok(applicationRegisterService.getLoanApplications(loanExtId))
}