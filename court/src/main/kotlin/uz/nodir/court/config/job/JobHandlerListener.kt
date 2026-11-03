package uz.nodir.court.config.job

import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobListener
import org.slf4j.LoggerFactory




class JobHandlerListener : JobListener {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun getName(): String {
        return "JobHandlerListener"
    }

    override fun jobToBeExecuted(p0: JobExecutionContext) {
        var presentRetriesCount = p0.mergedJobDataMap?.get(SchedulerConstants.CURRENT_RETRY_COUNT) as? Int ?: 0
        p0.put(SchedulerConstants.RETRIES, ++presentRetriesCount)
    }

    override fun jobExecutionVetoed(p0: JobExecutionContext?) {
        log.warn("jobExecutionVetoed: {}", p0)
    }

    override fun jobWasExecuted(p0: JobExecutionContext?, p1: JobExecutionException?) {
        p1?.let {
            log.error("job was executed with error: {}", it.message)
            return
        }
    }
}