package krllsv.tutor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import krllsv.tutor.api.dto.response.AsyncTaskDto;
import krllsv.tutor.api.service.AsyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/api/async")
@Tag(name = "Async Operations", description = "Асинхронные бизнес-операции")
public class AsyncController {

    private final AsyncService asyncService;

    public AsyncController(AsyncService asyncService) {
        this.asyncService = asyncService;
    }

    @PostMapping("/tutors/update-rates")
    @Operation(summary = "Асинхронное обновление ставок преподавателей")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Задача создана"),
                            @ApiResponse(responseCode = "400", description = "Некорректные параметры")
    })
    public ResponseEntity<Map<String, String>> updateTutorRatesAsync(
            @Parameter(description = "Процент повышения ставки")
            @RequestParam double percentageIncrease) {

        String taskId = asyncService.updateAllTutorRatesAsync(percentageIncrease);
        return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "message", "Async task started. Use GET /api/async/tasks/{taskId} to check status",
                "statusUrl", "/api/async/tasks/" + taskId
        ));
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Получить статус асинхронной задачи")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Статус получен"),
                            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<AsyncTaskDto> getTaskStatus(
            @Parameter(description = "ID задачи")
            @PathVariable String taskId) {

        AsyncTaskDto taskStatus = asyncService.getTaskStatus(taskId);
        return ResponseEntity.ok(taskStatus);
    }

    @GetMapping("/tasks")
    @Operation(summary = "Получить список всех задач")
    public ResponseEntity<Map<String, AsyncTaskDto>> getAllTasks() {
        return ResponseEntity.ok(asyncService.getAllTasks());
    }
}