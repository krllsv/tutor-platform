package krllsv.tutor.api.service;

import krllsv.tutor.api.dto.response.RaceConditionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class RaceConditionDemoService {

    private final Object lock = new Object();

    private long unsafeCounter = 0;
    private long syncCounter = 0;
    private final AtomicLong atomicCounter = new AtomicLong(0);

    public RaceConditionResult demonstrateRaceCondition(int threads, int incrementsPerThread)
            throws InterruptedException {

        unsafeCounter = 0;
        syncCounter = 0;
        atomicCounter.set(0);

        long expectedValue = (long) threads * incrementsPerThread;

        log.info("Запуск демонстрации race condition: {} потоков, {} инкрементов на поток = {} ожидается",
                threads, incrementsPerThread, expectedValue);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        unsafeCounter++;

                        synchronized (lock) {
                            syncCounter++;
                        }

                        atomicCounter.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long executionTimeMs = endTime - startTime;
        long lostValues = expectedValue - unsafeCounter;
        double lostPercentage = (lostValues * 100.0) / expectedValue;

        String conclusion = String.format(
                "Race condition: ожидалось %d, но unsafeCounter = %d (потеряно %d значений, %.2f%%). " +
                        "syncCounter и atomicCounter показали правильный результат %d.",
                expectedValue, unsafeCounter, lostValues, lostPercentage, syncCounter
        );

        log.info("=== РЕЗУЛЬТАТ ===");
        log.info("Ожидалось: {}", expectedValue);
        log.info("Unsafe counter: {} (потеряно {} значений, {}%)", unsafeCounter, lostValues, lostPercentage);
        log.info("Sync counter: {}", syncCounter);
        log.info("Atomic counter: {}", atomicCounter.get());
        log.info("Время выполнения: {} мс", executionTimeMs);
        log.info(conclusion);

        return new RaceConditionResult(
                threads,
                incrementsPerThread,
                expectedValue,
                unsafeCounter,
                syncCounter,
                atomicCounter.get(),
                lostValues,
                lostPercentage,
                conclusion,
                executionTimeMs
        );
    }

    public void resetAllCounters() {
        unsafeCounter = 0;
        syncCounter = 0;
        atomicCounter.set(0);
        log.info("Все счётчики сброшены");
    }
}