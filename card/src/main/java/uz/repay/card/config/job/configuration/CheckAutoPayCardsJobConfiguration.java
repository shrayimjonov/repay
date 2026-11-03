package uz.repay.card.config.job.configuration;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.repay.card.service.business.component.AutoPayToSVGateConverter;


@Configuration
public class CheckAutoPayCardsJobConfiguration {
    @Value("${spring.application.name}")
    private String GROUP_ID;

    @Bean
    public JobDetail checkAutoPayCardsToSVGateAccess() {
        return JobBuilder
                .newJob(AutoPayToSVGateConverter.class)
                .withIdentity("checkAutoPayCardsToSVGateAccessJob", GROUP_ID)
                .withDescription("Get all auto pay cards and check token to access in svGate")
                .storeDurably()
                .requestRecovery(false)
                .build();
    }

    @Bean
    public Trigger showTimeTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(checkAutoPayCardsToSVGateAccess())
                .withDescription("checkAutoPayCardsToSVGateAccess Trigger")
                .withIdentity("checkAutoPayCardsToSVGateAccessTrigger", GROUP_ID)
                .withSchedule(
                        CronScheduleBuilder
                                .cronSchedule("0 0 6 * * ?")
                                .withMisfireHandlingInstructionIgnoreMisfires()
                )
                .build();
    }


}
