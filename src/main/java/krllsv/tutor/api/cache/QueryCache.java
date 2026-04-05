package krllsv.tutor.api.cache;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class QueryCache {

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
            System.out.println("[CACHE HIT] " + endpoint + "?subject=" + subject + "&page=" + page);
            return (T) cache.get(key);
        }

        System.out.println("[CACHE MISS] " + endpoint + "?subject=" + subject + "&page=" + page);
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
        System.out.println("[CACHE PUT] " + endpoint + "?subject=" + subject + "&page=" + page);
    }

    public void invalidateByEndpoint(String endpoint) {
        cache.keySet().removeIf(key -> endpoint.equals(key.getEndpoint()));
        System.out.println("[CACHE INVALIDATED] " + endpoint);
    }
}