package uz.nodir.repayautoconfigure.configuration.cache;//package uz.repay.common.config.cache;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.nodir.repayautoconfigure.utils.CacheUtils;

@Configuration
@EnableCaching
@Slf4j
public class CacheableConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(CacheManager.class)
    @ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "none", matchIfMissing = false)
    public CacheManager ownCacheManager() {
        log.info("Default configure cache manager to SimpleCacheManager");
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(CacheUtils.getAllCaches());
        return cacheManager;
    }

}
