package uz.repay.card.config.job.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.enums.ResponseStatus;


@Configuration
@Slf4j
public class JobExecutionHistoryListener extends JobListenerSupport {

    @Override
    public String getName() {
        return "JobExecutionHistoryListener";
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JobDetail jobDetail = context.getJobDetail();
        String jobName = jobDetail.getKey().toString();

        boolean success = (jobException == null);
        var status = success ? ResponseStatus.SUCCESS : ResponseStatus.FAILED;

        log.info("Job [{}] executed with status: {}", jobName, status);
    }
}
