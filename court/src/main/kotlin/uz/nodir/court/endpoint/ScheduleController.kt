package uz.nodir.court.endpoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import uz.nodir.court.model.dto.court.request.CourtFilterRequestDTO
import uz.nodir.court.model.dto.court.request.CourtUpdateRequestDTO
import uz.nodir.court.model.dto.court.request.CreateLoanCourtRequestDTO
import uz.nodir.court.service.business.NotificationScheduleService
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.ResultData


@RestController
@RequestMapping("/api/v1/schedule", produces = ["application/json"], consumes = ["application/json"])
@Tag(name = "schedule-endpoint", description = "Schedule Endpoint")
@PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_COURT_DAY_SCHEDULE)")
class ScheduleController(

    private val notificationScheduleService: NotificationScheduleService
) {

    @PostMapping
    @Operation(
        summary = "Court day scheduler create",
        description = "set court day and take notification",
        operationId = "court-schedule-court-create"
    )
    fun create(@RequestBody @Valid requestDTO: CreateLoanCourtRequestDTO): ResponseEntity<ResultData<CoreError>> =
        ResponseEntity.ok(notificationScheduleService.create(requestDTO))

    @PostMapping("/filter")
    @Operation(
        summary = "Court day filter",
        description = "Filter by loan",
        operationId = "court-schedule-court-filter"
    )
    fun filterCourtByLoan(@RequestBody @Valid requestDTO: CourtFilterRequestDTO) =
        notificationScheduleService.filter(requestDTO)

    @PutMapping
    @Operation(
        summary = "Court day scheduler update",
        description = "Update court by id",
        operationId = "court-schedule-court-update"
    )
    fun update(@RequestBody @Valid requestDTO: CourtUpdateRequestDTO) =
        notificationScheduleService.update(requestDTO)

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Court day schedule delete",
        description = "Delete court schedule by id",
        operationId = "court-schedule-court-delete"
    )
    fun delete(@PathVariable("id") id: Long) =
        notificationScheduleService.delete(id)
}