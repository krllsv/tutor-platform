package krllsv.tutor.api.cache;

import lombok.Getter;
import java.util.Objects;

@Getter
public class QueryKey {
    private final String endpoint;
    private final String subject;
    private final int page;
    private final int size;
    private final String sortBy;
    private final String sortDir;

    public QueryKey(String endpoint, String subject, int page, int size, String sortBy, String sortDir) {
        this.endpoint = endpoint;
        this.subject = subject;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryKey that = (QueryKey) o;
        return page == that.page &&
                size == that.size &&
                Objects.equals(endpoint, that.endpoint) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(sortBy, that.sortBy) &&
                Objects.equals(sortDir, that.sortDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint, subject, page, size, sortBy, sortDir);
    }
}