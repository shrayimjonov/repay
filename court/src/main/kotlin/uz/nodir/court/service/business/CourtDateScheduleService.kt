package uz.nodir.court.service.business

import org.quartz.JobDetail
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.court.config.job.AbstractScheduler
import uz.nodir.court.service.SchedulerService
import uz.nodir.court.service.business.component.CourtAlertComponent
import uz.nodir.court.service.core.dao.CourtDAO
import uz.nodir.court.service.createSimpleTrigger
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState
import java.util.*



@Service
class CourtDateScheduleService(
    val schedulerService: SchedulerService,
    val courtAlertComponent: CourtAlertComponent,
    val courtDAO: CourtDAO,
) : AbstractScheduler() {

    companion object {
        const val RETRY_COUNT = 0
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun executeTask(context: JobExecutionContext?) {
        val jobDataMap = context!!.trigger.jobDataMap
        val message = jobDataMap.getString("message")
        val date = jobDataMap["date"]
        val loanExtId = jobDataMap["loanExtId"] as String?
        val phone = jobDataMap["phone"] as String?
        val id = jobDataMap["id"] as UUID?

        log.info("Scheduled job started at: $date with data: $message $date $loanExtId")

        send(id, loanExtId, phone, message)
    }


    fun scheduleCourtDay(
        message: String,
        date: Date,
        loanExtId: String,
        phone: String,
        id: UUID
    ): SimpleResponse<CoreError> {
        log.info("Build scheduler court by loan: $loanExtId in $date")


        val jobDataMap = getJobDataMap(RETRY_COUNT)
        jobDataMap["message"] = message
        jobDataMap["date"] = date
        jobDataMap["loanExtId"] = loanExtId
        jobDataMap["phone"] = phone
        jobDataMap["id"] = id


        val jobDetail: JobDetail = schedulerService.buildJobDetail(this.javaClass, jobDataMap)

        val trigger = createSimpleTrigger(
            jobDetail, id.toString(),
            jobDataMap, date
        )

        return schedulerService.schedule(jobDetail, trigger!!)
    }


    fun send(id: UUID?, loanExtID: String?, phone: String?, message: String?) {
        log.info("Start executing alert for id: {}", id)

        listOf(id, loanExtID, phone, message)
            .takeIf {
                it.all { param -> param != null }
            }?.let {

                val court = courtDAO.findByJobId(id!!)

                court?.let {
                    courtAlertComponent.sendNotification(
                        loanExtId = loanExtID!!,
                        message = message!!,
                        phone = phone!!
                    )
                } ?: {
                    "Court not found and not sent alert for id: $id"
                }

            } ?: log.error("Method finished with error: \"One or more parameters are null\"")

    }

    fun rescheduleCourtDay(
        message: String?,
        date: Date?,
        phone: String?,
        id: UUID?
    ): SimpleResponse<CoreError> {
        log.info("Reschedule court by id: $id in $date")

        listOf(id, date, phone, message)
            .takeIf {
                it.all { param -> param != null }
            }?.let {
                return schedulerService.reschedule(id!!, date!!, message!!, phone!!)
            } ?: {
            log.error("One or more parameters are null")
        }

        return SimpleResponse(ResultState.WrongParams.code, ResultState.WrongParams.message)
    }

    fun deleteScheduleCourtDay(
        id: UUID?
    ): SimpleResponse<CoreError> {
        log.info("Delete schedule court by id: $id")

        id ?: return SimpleResponse(ResultState.WrongParams.code, ResultState.WrongParams.message)

        id.let {
            return schedulerService.deleteSchedule(id)
        }
    }
}