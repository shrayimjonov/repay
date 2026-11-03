package uz.repay.card.config.job;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;


@Configuration
public class SchedulerConfiguration {
    @Value("${spring.application.name}")
    private String GROUP_ID;

    @Bean
    public Scheduler scheduler(List<Trigger> triggers, List<JobDetail> jobDetails, SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
        var scheduler = schedulerFactoryBean.getScheduler();
        revalidateJobs(jobDetails, scheduler);
        rescheduleTriggers(triggers, scheduler);
        scheduler.start();
        return scheduler;
    }

    private void rescheduleTriggers(List<Trigger> triggers, Scheduler scheduler) {
        triggers.forEach(t -> {
            try {
                JobKey jobKey = t.getJobKey();

                if (scheduler.checkExists(jobKey))
                    scheduler.rescheduleJob(t.getKey(), t);
                else
                    scheduler.scheduleJob(t);

            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void revalidateJobs(List<JobDetail> jobDetails, Scheduler scheduler) {
        var jobKeys = jobDetails.stream().map(JobDetail::getKey).toList();


        try {
            scheduler.getJobKeys(GroupMatcher.jobGroupEquals(GROUP_ID))
                    .forEach(s -> {
                                boolean b = !jobKeys.contains(s);

                                if (b) {
                                    try {
                                        scheduler.deleteJob(s);
                                    } catch (SchedulerException e) {
                                        throw new RuntimeException(e);
                                    }
                                }


                            }
                    );
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }
}
