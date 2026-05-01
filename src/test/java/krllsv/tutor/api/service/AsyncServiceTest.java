package krllsv.tutor.api.service;

import krllsv.tutor.api.dto.response.AsyncTaskDto;
import krllsv.tutor.api.entity.TutorEntity;
import krllsv.tutor.api.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private AsyncService asyncService;

    private List<TutorEntity> tutors;

    @BeforeEach
    void setUp() {
        TutorEntity tutor1 = new TutorEntity();
        tutor1.setId(1L);
        tutor1.setFirstName("Иван");
        tutor1.setLastName("Петров");
        tutor1.setHourlyRate(new BigDecimal("1000"));
        tutor1.setEmail("ivan@test.com");

        TutorEntity tutor2 = new TutorEntity();
        tutor2.setId(2L);
        tutor2.setFirstName("Мария");
        tutor2.setLastName("Иванова");
        tutor2.setHourlyRate(new BigDecimal("1500"));
        tutor2.setEmail("maria@test.com");

        tutors = List.of(tutor1, tutor2);
    }

    @Test
    void updateAllTutorRatesAsync_ShouldReturnTaskId() {
        String taskId = asyncService.updateAllTutorRatesAsync(10.0);

        assertNotNull(taskId);
        assertFalse(taskId.isEmpty());
    }

    @Test
    void updateAllTutorRatesAsync_ShouldReturnUniqueTaskId() {
        String taskId1 = asyncService.updateAllTutorRatesAsync(10.0);
        String taskId2 = asyncService.updateAllTutorRatesAsync(20.0);

        assertNotEquals(taskId1, taskId2);
    }

    @Test
    void getTaskStatus_WithValidId_ShouldReturnTask() throws InterruptedException {
        String taskId = asyncService.updateAllTutorRatesAsync(10.0);

        Thread.sleep(100);

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);

        assertNotNull(task);
        assertEquals(taskId, task.getTaskId());
        assertNotNull(task.getStatus());
        assertNotNull(task.getOperationType());
    }

    @Test
    void getTaskStatus_WithInvalidId_ShouldThrowException() {
        String invalidTaskId = "invalid-id-123";

        assertThrows(IllegalArgumentException.class, () -> asyncService.getTaskStatus(invalidTaskId));
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        asyncService.updateAllTutorRatesAsync(10.0);
        asyncService.updateAllTutorRatesAsync(20.0);

        Map<String, AsyncTaskDto> allTasks = asyncService.getAllTasks();

        assertNotNull(allTasks);
        assertEquals(2, allTasks.size());
    }

    @Test
    void processTutorRateUpdate_ShouldUpdateRates_WhenTutorsExist() throws Exception {
        when(tutorRepository.findAll()).thenReturn(tutors);

        String taskId = asyncService.updateAllTutorRatesAsync(10.0);

        Thread.sleep(500);

        verify(tutorRepository, atLeastOnce()).save(any(TutorEntity.class));
    }

    @Test
    void processTutorRateUpdate_ShouldHandleEmptyTutorList() throws Exception {
        when(tutorRepository.findAll()).thenReturn(List.of());

        String taskId = asyncService.updateAllTutorRatesAsync(10.0);

        Thread.sleep(100);

        AsyncTaskDto task = asyncService.getTaskStatus(taskId);
        assertNotNull(task);
        verify(tutorRepository, never()).save(any(TutorEntity.class));
    }

    @Test
    void getTaskStatus_ShouldReturnCorrectProgress() throws InterruptedException {
        when(tutorRepository.findAll()).thenReturn(tutors);

        String taskId = asyncService.updateAllTutorRatesAsync(10.0);

        Thread.sleep(100);
        AsyncTaskDto task = asyncService.getTaskStatus(taskId);

        assertNotNull(task);
        assertNotNull(task.getProgress());
    }
}