package krllsv.tutor.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CounterServiceTest {

    private CounterService counterService;

    @BeforeEach
    void setUp() {
        counterService = new CounterService();
    }

    @Test
    void incrementSync_ShouldIncreaseCounter() {
        long value1 = counterService.incrementSync();
        long value2 = counterService.incrementSync();
        long value3 = counterService.incrementSync();

        assertThat(value1).isEqualTo(1);
        assertThat(value2).isEqualTo(2);
        assertThat(value3).isEqualTo(3);
    }

    @Test
    void getSyncCounter_ShouldReturnCurrentValue() {
        assertThat(counterService.getSyncCounter()).isZero();

        counterService.incrementSync();
        assertThat(counterService.getSyncCounter()).isEqualTo(1);

        counterService.incrementSync();
        assertThat(counterService.getSyncCounter()).isEqualTo(2);
    }

    @Test
    void resetSyncCounter_ShouldResetToZero() {
        counterService.incrementSync();
        counterService.incrementSync();
        assertThat(counterService.getSyncCounter()).isEqualTo(2);

        counterService.resetSyncCounter();
        assertThat(counterService.getSyncCounter()).isZero();
    }

    @Test
    void incrementAtomic_ShouldIncreaseCounter() {
        long value1 = counterService.incrementAtomic();
        long value2 = counterService.incrementAtomic();
        long value3 = counterService.incrementAtomic();

        assertThat(value1).isEqualTo(1);
        assertThat(value2).isEqualTo(2);
        assertThat(value3).isEqualTo(3);
    }

    @Test
    void getAtomicCounter_ShouldReturnCurrentValue() {
        assertThat(counterService.getAtomicCounter()).isZero();

        counterService.incrementAtomic();
        assertThat(counterService.getAtomicCounter()).isEqualTo(1);

        counterService.incrementAtomic();
        assertThat(counterService.getAtomicCounter()).isEqualTo(2);
    }

    @Test
    void resetAtomicCounter_ShouldResetCorrectlyAfterVariousOperations() {
        assertThat(counterService.getAtomicCounter()).isZero();

        counterService.incrementAtomic();
        counterService.incrementAtomic();
        assertThat(counterService.getAtomicCounter()).isEqualTo(2);

        counterService.resetAtomicCounter();
        assertThat(counterService.getAtomicCounter()).isZero();

        counterService.incrementAtomic();
        assertThat(counterService.getAtomicCounter()).isEqualTo(1);

        counterService.resetAtomicCounter();
        assertThat(counterService.getAtomicCounter()).isZero();
    }

    @Test
    void reset_ShouldResetBothCounters() {
        counterService.incrementSync();
        counterService.incrementAtomic();
        counterService.incrementAtomic();

        assertThat(counterService.getSyncCounter()).isEqualTo(1);
        assertThat(counterService.getAtomicCounter()).isEqualTo(2);

        counterService.reset();

        assertThat(counterService.getSyncCounter()).isZero();
        assertThat(counterService.getAtomicCounter()).isZero();
    }

    @Test
    void syncCounter_ShouldBeThreadSafe() throws InterruptedException {
        counterService.reset();

        int threadCount = 50;
        int incrementsPerThread = 100;
        int expectedValue = threadCount * incrementsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counterService.incrementSync();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(counterService.getSyncCounter()).isEqualTo(expectedValue);
    }

    @Test
    void atomicCounter_ShouldBeThreadSafe() throws InterruptedException {
        counterService.reset();

        int threadCount = 50;
        int incrementsPerThread = 100;
        int expectedValue = threadCount * incrementsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counterService.incrementAtomic();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(counterService.getAtomicCounter()).isEqualTo(expectedValue);
    }

    @Test
    void counters_ShouldBeIndependent() {
        counterService.incrementSync();
        counterService.incrementSync();
        counterService.incrementAtomic();

        assertThat(counterService.getSyncCounter()).isEqualTo(2);
        assertThat(counterService.getAtomicCounter()).isEqualTo(1);
    }

    @Test
    void resetSyncCounter_WithSynchronized_ShouldWorkCorrectly() {
        counterService.incrementSync();
        counterService.incrementSync();
        counterService.incrementSync();

        assertThat(counterService.getSyncCounter()).isEqualTo(3);

        counterService.resetSyncCounter();

        assertThat(counterService.getSyncCounter()).isZero();
    }

    @Test
    void resetAtomicCounter_ShouldWorkCorrectly() {
        counterService.incrementAtomic();
        counterService.incrementAtomic();

        assertThat(counterService.getAtomicCounter()).isEqualTo(2);

        counterService.resetAtomicCounter();

        assertThat(counterService.getAtomicCounter()).isZero();
    }
}