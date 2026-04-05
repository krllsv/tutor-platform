package krllsv.tutor.api.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class QueryCache {

    private static final Logger LOG = LoggerFactory.getLogger(QueryCache.class);
    private final Map<QueryKey, Object> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(String endpoint,
                     String subject,
                     int page,
                     int size,
                     String sortBy,
                     String sortDir,
                     Supplier<T> dbQuery
    ) {
        QueryKey key = new QueryKey(endpoint, subject, page, size, sortBy, sortDir);

        if (cache.containsKey(key)) {
            LOG.info("[CACHE HIT] {}?page={}", endpoint, page);
            return (T) cache.get(key);
        }

        LOG.info("[CACHE MISS] {}?page={}", endpoint, page);
        T result = dbQuery.get();
        put(endpoint, subject, page, size, sortBy, sortDir, result);
        return result;
    }

    public void put(String endpoint,
                    String subject,
                    int page,
                    int size,
                    String sortBy,
                    String sortDir,
                    Object result
    ) {
        QueryKey key = new QueryKey(endpoint, subject, page, size, sortBy, sortDir);
        cache.put(key, result);
        LOG.info("[CACHE PUT] {}&page={}", endpoint, page);
    }

    public void invalidateByEndpoint(String endpoint) {
        cache.keySet().removeIf(key -> endpoint.equals(key.getEndpoint()));
        LOG.info("[CACHE INVALIDATED] {}", endpoint);
    }
}