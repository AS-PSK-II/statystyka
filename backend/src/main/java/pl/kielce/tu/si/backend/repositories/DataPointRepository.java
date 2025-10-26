package pl.kielce.tu.si.backend.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import pl.kielce.tu.si.backend.DataPoint;
import pl.kielce.tu.si.backend.config.CacheConfig;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DataPointRepository {

    private final CacheManager cacheManager;

    @SuppressWarnings("unchecked")
    public List<DataPoint> getAllDataPoints() {
        Cache cache = cacheManager.getCache(CacheConfig.DATA_POINTS_CACHE);
        return cache != null ? cache.get(CacheConfig.DATA_POINTS_CACHE_KEY, List.class) : null;
    }
}
