package pl.kielce.tu.si.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String DATA_POINTS_CACHE = "dataPoints";
    public static final String DATA_POINTS_CACHE_KEY = "dataPoints";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(DATA_POINTS_CACHE);
    }
}
