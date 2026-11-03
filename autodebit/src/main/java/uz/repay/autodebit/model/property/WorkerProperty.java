package uz.repay.autodebit.model.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "internal.worker", ignoreUnknownFields = false)
public class WorkerProperty {
    public Long minimumDebitAmount;
    public Integer subListSize;
    public Integer sleepAfterFinish;
    public Integer sleepForInsolvent;

}
