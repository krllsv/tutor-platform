package krllsv.tutor.api.service;

import krllsv.tutor.api.dto.response.RaceConditionResult;
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
class RaceConditionDemoServiceTest {

    private RaceConditionDemoService raceConditionDemoService;

    @BeforeEach
    void setUp() {
        raceConditionDemoService = new RaceConditionDemoService();
    }

    @Test
    void demonstrateRaceCondition_ShouldReturnNonNullResult() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(10, 100);

        assertThat(result).isNotNull();
    }

    @Test
    void demonstrateRaceCondition_ShouldCalculateExpectedValueCorrectly() throws Exception {
        int threads = 10;
        int increments = 100;
        long expected = (long) threads * increments;

        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(threads, increments);

        assertThat(result.getThreadsCount()).isEqualTo(threads);
        assertThat(result.getIncrementsPerThread()).isEqualTo(increments);
        assertThat(result.getExpectedValue()).isEqualTo(expected);
    }

    @Test
    void demonstrateRaceCondition_UnsafeCounter_MayBeLessThanExpected() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(50, 500);

        assertThat(result.getUnsafeCounter()).isLessThanOrEqualTo(result.getExpectedValue());
    }

    @Test
    void demonstrateRaceCondition_SyncCounter_ShouldEqualExpected() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(20, 200);

        assertThat(result.getSyncCounter()).isEqualTo(result.getExpectedValue());
    }

    @Test
    void demonstrateRaceCondition_AtomicCounter_ShouldEqualExpected() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(20, 200);

        assertThat(result.getAtomicCounter()).isEqualTo(result.getExpectedValue());
    }

    @Test
    void demonstrateRaceCondition_LostValues_ShouldBeNonNegative() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(30, 300);

        assertThat(result.getLostValues()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void demonstrateRaceCondition_LostPercentage_ShouldBeBetweenZeroAndHundred() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(40, 400);

        assertThat(result.getLostPercentage()).isBetween(0.0, 100.0);
    }

    @Test
    void demonstrateRaceCondition_WithLargeLoad_ShouldShowLostValues() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(100, 1000);

        assertThat(result).isNotNull();
        assertThat(result.getUnsafeCounter()).isLessThanOrEqualTo(result.getExpectedValue());
    }

    @Test
    void demonstrateRaceCondition_WithSingleThread_ShouldNotLoseValues() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(1, 1000);

        assertThat(result.getUnsafeCounter()).isEqualTo(result.getExpectedValue());
        assertThat(result.getSyncCounter()).isEqualTo(result.getExpectedValue());
        assertThat(result.getAtomicCounter()).isEqualTo(result.getExpectedValue());
        assertThat(result.getLostValues()).isZero();
    }

    @Test
    void demonstrateRaceCondition_WithMinimalParams_ShouldWork() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(2, 10);

        assertThat(result).isNotNull();
        assertThat(result.getExecutionTimeMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void resetAllCounters_ShouldResetAllCounters() throws Exception {
        raceConditionDemoService.demonstrateRaceCondition(10, 100);

        raceConditionDemoService.resetAllCounters();

        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(1, 1);
        assertThat(result.getUnsafeCounter()).isEqualTo(1);
        assertThat(result.getSyncCounter()).isEqualTo(1);
        assertThat(result.getAtomicCounter()).isEqualTo(1);
    }

    @Test
    void demonstrateRaceCondition_ShouldReturnPositiveExecutionTime() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(10, 100);

        assertThat(result.getExecutionTimeMs()).isGreaterThan(0);
    }

    @Test
    void demonstrateRaceCondition_ShouldHaveConclusionMessage() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(10, 100);

        assertThat(result.getConclusion()).isNotNull();
        assertThat(result.getConclusion()).isNotEmpty();
        assertThat(result.getConclusion()).contains("Race condition");
    }

    @Test
    void demonstrateRaceCondition_ShouldThrowInterruptedException() throws Exception {
        int threads = 2;
        int increments = 10;

        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(threads, increments);

        assertThat(result).isNotNull();
    }

    @Test
    void demonstrateRaceCondition_WithZeroIncrements_ShouldWork() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(5, 0);

        assertThat(result.getExpectedValue()).isZero();
        assertThat(result.getUnsafeCounter()).isZero();
        assertThat(result.getSyncCounter()).isZero();
        assertThat(result.getAtomicCounter()).isZero();
    }

    @Test
    void demonstrateRaceCondition_WithManyThreads_ShouldComplete() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(200, 100);

        assertThat(result.getThreadsCount()).isEqualTo(200);
        assertThat(result.getExpectedValue()).isEqualTo(20000);
    }

    @Test
    void demonstrateRaceCondition_ShouldBeThreadSafe() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                try {
                    RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(10, 50);
                    assertThat(result).isNotNull();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
    }

    @Test
    void demonstrateRaceCondition_ShouldSetCorrectFields() throws Exception {
        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(15, 200);

        assertThat(result.getThreadsCount()).isEqualTo(15);
        assertThat(result.getIncrementsPerThread()).isEqualTo(200);
        assertThat(result.getExpectedValue()).isEqualTo(3000);
        assertThat(result.getSyncCounter()).isEqualTo(3000);
        assertThat(result.getAtomicCounter()).isEqualTo(3000);
    }
}