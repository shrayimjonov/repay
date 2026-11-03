package uz.davrbank.repay.loggingrest.configuration;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import uz.davrbank.repay.commonlogging.config.LoggingAutoConfiguration;

@Configuration
@ImportAutoConfiguration(LoggingAutoConfiguration.class)
public class LoggingConfiguration {
}
