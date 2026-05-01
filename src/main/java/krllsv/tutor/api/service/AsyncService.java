package krllsv.tutor.api.service;

import krllsv.tutor.api.dto.response.AsyncTaskDto;
import krllsv.tutor.api.entity.TutorEntity;
import krllsv.tutor.api.repository.TutorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AsyncService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";

    private static final String OPERATION_BULK_RATE_UPDATE = "BULK_RATE_UPDATE";

    private final Map<String, AsyncTaskDto> taskStore = new ConcurrentHashMap<>();
    private final TutorRepository tutorRepository;
    private final AsyncService self;

    @Autowired
    public AsyncService(TutorRepository tutorRepository, @Lazy AsyncService self) {
        this.tutorRepository = tutorRepository;
        this.self = self;
    }

    private String createTask(String operationType) {
        String taskId = UUID.randomUUID().toString();
        AsyncTaskDto task = new AsyncTaskDto();
        task.setTaskId(taskId);
        task.setStatus(STATUS_PENDING);
        task.setOperationType(operationType);
        task.setProgress(0);
        task.setMessage("Task created, waiting to start");
        task.setCreatedAt(LocalDateTime.now());
        taskStore.put(taskId, task);
        log.info("New task created: {} for operation: {}", taskId, operationType);
        return taskId;
    }

    public AsyncTaskDto getTaskStatus(String taskId) {
        AsyncTaskDto task = taskStore.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }
        return task;
    }

    private void updateTaskStatus(String taskId, String status, Integer progress, String message) {
        AsyncTaskDto task = taskStore.get(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setProgress(progress);
            task.setMessage(message);
            if (status.equals(STATUS_COMPLETED) || status.equals(STATUS_FAILED)) {
                task.setCompletedAt(LocalDateTime.now());
            }
            log.info("Task {} updated: status={}, progress={}%", taskId, status, progress);
        }
    }

    private void updateTaskResult(String taskId, Object result) {
        AsyncTaskDto task = taskStore.get(taskId);
        if (task != null) {
            task.setResult(result);
        }
    }

    private void updateTaskError(String taskId, String errorMessage) {
        AsyncTaskDto task = taskStore.get(taskId);
        if (task != null) {
            task.setErrorMessage(errorMessage);
            task.setStatus(STATUS_FAILED);
            task.setCompletedAt(LocalDateTime.now());
            log.error("Task {} failed: {}", taskId, errorMessage);
        }
    }

    public Map<String, AsyncTaskDto> getAllTasks() {
        return new ConcurrentHashMap<>(taskStore);
    }

    public String updateAllTutorRatesAsync(double percentageIncrease) {
        String taskId = createTask(OPERATION_BULK_RATE_UPDATE);
        self.processTutorRateUpdate(taskId, percentageIncrease);
        return taskId;
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> processTutorRateUpdate(String taskId, double percentageIncrease) {
        log.info("Starting async rate update for tutors, taskId: {}, increase: {}%", taskId, percentageIncrease);

        try {
            updateTaskStatus(taskId, STATUS_IN_PROGRESS, 0, "Starting rate update...");

            java.util.List<TutorEntity> tutors = tutorRepository.findAll();
            int total = tutors.size();

            if (total == 0) {
                updateTaskStatus(taskId, STATUS_COMPLETED, 100, "No tutors found");
                updateTaskResult(taskId, "No tutors in database");
                return CompletableFuture.completedFuture(null);
            }

            int processed = 0;

            for (TutorEntity tutor : tutors) {
                double newRate = tutor.getHourlyRate().doubleValue() * (1 + percentageIncrease / 100);
                tutor.setHourlyRate(java.math.BigDecimal.valueOf(newRate));
                tutorRepository.save(tutor);

                processed++;

                if (!sleepWithInterruptionHandling(taskId, 1000)) {
                    return CompletableFuture.completedFuture(null);
                }

                int progress = (processed * 100) / total;
                if (shouldUpdateStatus(progress, processed, total)) {
                    updateTaskStatus(taskId, STATUS_IN_PROGRESS, progress,
                            String.format("Processed %d of %d tutors", processed, total));
                }
            }

            String result = String.format("Successfully updated rates for %d tutors. Increase: %.1f%%",
                    total, percentageIncrease);
            updateTaskStatus(taskId, STATUS_COMPLETED, 100, "Rate update completed");
            updateTaskResult(taskId, result);

            log.info("Async rate update completed for task: {}", taskId);

        } catch (Exception e) {
            log.error("Error in async rate update: {}", e.getMessage(), e);
            updateTaskError(taskId, e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    private boolean sleepWithInterruptionHandling(String taskId, long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Task {} was interrupted during sleep", taskId, e);
            updateTaskError(taskId, "Task was interrupted: " + e.getMessage());
            return false;
        }
    }

    private boolean shouldUpdateStatus(int progress, int processed, int total) {
        return progress % 10 == 0 || processed == total;
    }
}