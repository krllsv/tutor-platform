package krllsv.tutor.api.service;

import krllsv.tutor.api.dto.response.AsyncTaskDto;
import krllsv.tutor.api.entity.TutorEntity;
import krllsv.tutor.api.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    private AsyncService asyncService;

    @BeforeEach
    void setUp() {
        asyncService = new AsyncService(tutorRepository, null);
    }

    @Test
    void createTask_ShouldCreateTaskWithPendingStatus() {
        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST_OPERATION");

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);

        assertThat(task)
                .isNotNull()
                .satisfies(t -> {
                    assertThat(t.getTaskId()).isEqualTo(taskId);
                    assertThat(t.getStatus()).isEqualTo("PENDING");
                    assertThat(t.getOperationType()).isEqualTo("TEST_OPERATION");
                    assertThat(t.getProgress()).isZero();
                    assertThat(t.getMessage()).isEqualTo("Task created, waiting to start");
                    assertThat(t.getCreatedAt()).isNotNull();
                    assertThat(t.getCompletedAt()).isNull();
                });
    }

    @Test
    void getTaskStatus_WhenTaskExists_ShouldReturnStatus() {
        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST");

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);

        assertThat(task).isNotNull();
        assertThat(task.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void getTaskStatus_WhenTaskDoesNotExist_ShouldThrowException() {
        assertThatThrownBy(() -> asyncService.getTaskStatus("non-existent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task not found with id: non-existent");
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST1");
        ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST2");
        ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST3");

        Map<String, AsyncTaskDto> tasks = asyncService.getAllTasks();

        assertThat(tasks).hasSize(3);
    }

    @Test
    void updateTaskStatus_WhenTaskExists_ShouldUpdateFields() {
        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST");

        ReflectionTestUtils.invokeMethod(asyncService, "updateTaskStatus",
                taskId, "IN_PROGRESS", 50, "Half done");

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);
        assertThat(task.getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(task.getProgress()).isEqualTo(50);
        assertThat(task.getMessage()).isEqualTo("Half done");
    }

    @Test
    void updateTaskStatus_WhenCompleted_ShouldSetCompletedAt() {
        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST");

        ReflectionTestUtils.invokeMethod(asyncService, "updateTaskStatus",
                taskId, "COMPLETED", 100, "Done");

        AsyncTaskDto taskAfter = asyncService.getTaskStatus(taskId);
        assertThat(taskAfter.getStatus()).isEqualTo("COMPLETED");
        assertThat(taskAfter.getCompletedAt()).isNotNull();
    }

    @Test
    void updateTaskStatus_WhenFailed_ShouldSetCompletedAt() {
        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST");

        ReflectionTestUtils.invokeMethod(asyncService, "updateTaskStatus",
                taskId, "FAILED", 0, "Failed");

        AsyncTaskDto taskAfter = asyncService.getTaskStatus(taskId);
        assertThat(taskAfter.getStatus()).isEqualTo("FAILED");
        assertThat(taskAfter.getCompletedAt()).isNotNull();
    }

    @Test
    void updateTaskStatus_ShouldDoNothing_WhenTaskNotFound() {
        ReflectionTestUtils.invokeMethod(asyncService, "updateTaskStatus",
                "non-existent-id", "RUNNING", 50, "Test message");

        Map<String, AsyncTaskDto> allTasks = asyncService.getAllTasks();
        assertThat(allTasks.containsKey("non-existent-id")).isFalse();
    }

    @Test
    void updateTaskResult_WhenTaskExists_ShouldSetResult() {
        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST");

        ReflectionTestUtils.invokeMethod(asyncService, "updateTaskResult", taskId, "Test Result");

        AsyncTaskDto taskAfter = asyncService.getTaskStatus(taskId);
        assertThat(taskAfter.getResult()).isEqualTo("Test Result");
    }

    @Test
    void updateTaskResult_ShouldDoNothing_WhenTaskNotFound() {
        ReflectionTestUtils.invokeMethod(asyncService, "updateTaskResult",
                "non-existent-id", "Some result");

        Map<String, AsyncTaskDto> allTasks = asyncService.getAllTasks();
        assertThat(allTasks.containsKey("non-existent-id")).isFalse();
    }

    @Test
    void updateTaskError_WhenTaskExists_ShouldSetFailedStatus() {
        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST");

        ReflectionTestUtils.invokeMethod(asyncService, "updateTaskError", taskId, "Test error");

        AsyncTaskDto taskAfter = asyncService.getTaskStatus(taskId);
        assertThat(taskAfter.getStatus()).isEqualTo("FAILED");
        assertThat(taskAfter.getErrorMessage()).isEqualTo("Test error");
        assertThat(taskAfter.getCompletedAt()).isNotNull();
    }

    @Test
    void updateTaskError_ShouldDoNothing_WhenTaskNotFound() {
        ReflectionTestUtils.invokeMethod(asyncService, "updateTaskError",
                "non-existent-id", "Some error");

        Map<String, AsyncTaskDto> allTasks = asyncService.getAllTasks();
        assertThat(allTasks.containsKey("non-existent-id")).isFalse();
    }

    @Test
    void processTutorRateUpdate_WhenNoTutors_ShouldCompleteSuccessfully() throws Exception {
        when(tutorRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "BULK_RATE_UPDATE");

        CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                asyncService.processTutorRateUpdate(taskId, 10.0)
        );

        future.get(2, TimeUnit.SECONDS);

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);
        assertThat(task.getStatus()).isEqualTo("COMPLETED");
        assertThat(task.getProgress()).isEqualTo(100);
        assertThat(task.getResult()).isEqualTo("No tutors in database");
    }

    @Test
    void processTutorRateUpdate_WhenExceptionOccurs_ShouldSetFailedStatus() throws Exception {
        when(tutorRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "BULK_RATE_UPDATE");

        CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                asyncService.processTutorRateUpdate(taskId, 10.0)
        );

        future.get(2, TimeUnit.SECONDS);

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);
        assertThat(task.getStatus()).isEqualTo("FAILED");
        assertThat(task.getErrorMessage()).contains("Database error");
    }

    @Test
    void processTutorRateUpdate_WithMultipleTutors_ShouldUpdateAll() throws Exception {
        when(tutorRepository.findAll()).thenReturn(List.of(
                createTutorEntity(1L, "Иван", "Петров"),
                createTutorEntity(2L, "Мария", "Иванова"),
                createTutorEntity(3L, "Петр", "Сидоров")
        ));

        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "BULK_RATE_UPDATE");

        CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                asyncService.processTutorRateUpdate(taskId, 10.0)
        );

        future.get(5, TimeUnit.SECONDS);

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);
        assertThat(task.getStatus()).isEqualTo("COMPLETED");
        assertThat(task.getProgress()).isEqualTo(100);
        assertThat(task.getResult().toString()).contains("Successfully updated rates for 3 tutors");
    }

    @Test
    void processTutorRateUpdate_With7Tutors_CoversProcessedEqualsTotal() throws Exception {
        List<TutorEntity> tutors = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            tutors.add(createTutorEntity((long) i, "Tutor" + i, "Test" + i));
        }
        when(tutorRepository.findAll()).thenReturn(tutors);

        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "BULK_RATE_UPDATE");

        CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                asyncService.processTutorRateUpdate(taskId, 10.0)
        );

        future.get(15, TimeUnit.SECONDS);

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);
        assertThat(task.getStatus()).isEqualTo("COMPLETED");
        assertThat(task.getProgress()).isEqualTo(100);
    }

    @Test
    void processTutorRateUpdate_With10Tutors_CoversProgressModulo10() throws Exception {
        List<TutorEntity> tutors = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tutors.add(createTutorEntity((long) i, "Tutor" + i, "Test" + i));
        }
        when(tutorRepository.findAll()).thenReturn(tutors);

        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "BULK_RATE_UPDATE");

        CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                asyncService.processTutorRateUpdate(taskId, 10.0)
        );

        future.get(15, TimeUnit.SECONDS);

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);
        assertThat(task.getStatus()).isEqualTo("COMPLETED");
        assertThat(task.getProgress()).isEqualTo(100);
    }

    @Test
    void sleepWithInterruptionHandling_WhenNormal_ShouldReturnTrue() {
        String taskId = "test-task-id";

        Boolean result = ReflectionTestUtils.invokeMethod(asyncService,
                "sleepWithInterruptionHandling", taskId, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void sleepWithInterruptionHandling_WhenInterrupted_ShouldReturnFalse() {
        String taskId = "test-task-id";

        Thread.currentThread().interrupt();

        Boolean result = ReflectionTestUtils.invokeMethod(asyncService,
                "sleepWithInterruptionHandling", taskId, 10L);

        assertThat(result).isFalse();

        Thread.interrupted();
    }

    @Test
    void startTask_ThreadSafe_ShouldGenerateUniqueIdsUnderConcurrency() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        String[] taskIds = new String[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread(() -> {
                taskIds[index] = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "TEST");
                latch.countDown();
            }).start();
        }

        latch.await(5, TimeUnit.SECONDS);

        for (int i = 0; i < threadCount; i++) {
            assertThat(taskIds[i]).isNotNull();
        }

        Map<String, AsyncTaskDto> tasks = asyncService.getAllTasks();
        assertThat(tasks).hasSize(threadCount);
    }

    @Test
    void updateAllTutorRatesAsync_ShouldCreateTaskAndCallSelf() {
        AsyncService spyService = spy(new AsyncService(tutorRepository, null));
        AsyncService selfMock = mock(AsyncService.class);
        ReflectionTestUtils.setField(spyService, "self", selfMock);

        String taskId = spyService.updateAllTutorRatesAsync(10.0);

        assertThat(taskId).isNotNull();
        verify(selfMock).processTutorRateUpdate(anyString(), anyDouble());
    }

    private TutorEntity createTutorEntity(Long id, String firstName, String lastName) {
        TutorEntity tutor = new TutorEntity();
        tutor.setId(id);
        tutor.setFirstName(firstName);
        tutor.setLastName(lastName);
        tutor.setHourlyRate(BigDecimal.valueOf(1000));
        tutor.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.com");
        return tutor;
    }

    @Test
    void processTutorRateUpdate_WhenSleepInterrupted_ShouldReturnEarly() throws Exception {
        List<TutorEntity> tutors = List.of(
                createTutorEntity(1L, "Иван", "Петров"),
                createTutorEntity(2L, "Мария", "Иванова")
        );
        when(tutorRepository.findAll()).thenReturn(tutors);

        String taskId = ReflectionTestUtils.invokeMethod(asyncService, "createTask", "BULK_RATE_UPDATE");

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            Thread.currentThread().interrupt();
            asyncService.processTutorRateUpdate(taskId, 10.0);
        });

        future.get(3, TimeUnit.SECONDS);

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);
        assertThat(task.getStatus()).isEqualTo("FAILED");
    }

    @Test
    void shouldUpdateStatus_WhenProgressMultipleOf10_ShouldReturnTrue() {
        boolean result = ReflectionTestUtils.invokeMethod(asyncService,
                "shouldUpdateStatus", 30, 3, 10);
        assertThat(result).isTrue();
    }

    @Test
    void shouldUpdateStatus_WhenProcessedEqualsTotal_ShouldReturnTrue() {
        boolean result = ReflectionTestUtils.invokeMethod(asyncService,
                "shouldUpdateStatus", 95, 10, 10);
        assertThat(result).isTrue();
    }

    @Test
    void shouldUpdateStatus_WhenBothConditionsFalse_ShouldReturnFalse() {
        boolean result = ReflectionTestUtils.invokeMethod(asyncService,
                "shouldUpdateStatus", 25, 3, 10);
        assertThat(result).isFalse();
    }
}