package uz.nodir.repayautoconfigure.configuration.database;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableMongoRepositories(value = {"uz.repay.*", "uz.nodir.*"})
@Slf4j
public class MongoConfiguration {


    @PostConstruct
    public void init() {
        log.info("MongoDB Configuration is enabled!");
    }
}
