package uz.repay.card.config.job.listener;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QuartzListenerConfig {

    @Bean
    public Scheduler schedulerListener(Scheduler scheduler, JobExecutionHistoryListener jobListener) throws SchedulerException {
        scheduler.getListenerManager().addJobListener(jobListener);
        return scheduler;
    };
}
