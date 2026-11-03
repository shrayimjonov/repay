package uz.nodir.repayautoconfigure.configuration.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.nodir.repayautoconfigure.model.property.RedisProperty;


@Configuration
@Slf4j
@RequiredArgsConstructor
@ConditionalOnClass(RedissonClient.class)
public class RedissonConfiguration {

    private final RedisProperty property;

    @Bean
    public RedissonClient redissonClient() {
        var url = "redis://%s:%d".formatted(property.getHost(), property.getPort());
        log.info("Register redisson client with url: {}", url);

        Config config = new Config();
        config
                .useSingleServer()
                .setAddress(url)
                .setPassword(property.getPassword());

        return Redisson.create(config);
    }
}
