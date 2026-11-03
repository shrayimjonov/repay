package uz.nodir.repayautoconfigure.configuration.database;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import uz.nodir.repayautoconfigure.model.property.RedisProperty;


/**
 * @group: DavrCoders
 **/

@Configuration
@EnableRedisRepositories(
        basePackages = {
                "uz.repay.*",
                "uz.nodir.*"
        },
        enableKeyspaceEvents
                = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.data.redis", name = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
@EnableConfigurationProperties(value = RedisProperty.class)
public class RedisConfig {
    private final RedisProperty redisProperty;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        var config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperty.getHost());
        config.setPort(redisProperty.getPort());
        log.info("Start redis connection factory with URL: {}", redisProperty.getHost() + ":" + redisProperty.getPort());
        config.setPassword(RedisPassword.of(redisProperty.getPassword()));
        return new LettuceConnectionFactory(config);
    }

}
