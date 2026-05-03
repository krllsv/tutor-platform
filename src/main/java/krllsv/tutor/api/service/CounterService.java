package krllsv.tutor.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class CounterService {

    private final Object object =  new Object();
    private long syncCounter = 0;
    private final AtomicLong atomicCounter = new AtomicLong(0);

    public long incrementSync() {
        synchronized (object) {
            syncCounter++;
            log.debug("Sync counter: {}", syncCounter);
            return syncCounter;
        }
    }

    public long getSyncCounter() {
        synchronized (object) {
            return syncCounter;
        }
    }

    public void resetSyncCounter() {
        synchronized (object) {
            syncCounter = 0;
            log.info("Sync counter reset to 0");
        }
    }

    public long incrementAtomic() {
        long value = atomicCounter.incrementAndGet();
        log.debug("Atomic counter: {}", value);
        return value;
    }

    public long getAtomicCounter() {
        return atomicCounter.get();
    }

    public void resetAtomicCounter() {
        atomicCounter.set(0);
        log.info("Atomic counter reset to 0");
    }

    public void reset() {
        resetSyncCounter();
        resetAtomicCounter();
    }
}