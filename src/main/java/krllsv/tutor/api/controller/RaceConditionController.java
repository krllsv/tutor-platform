package krllsv.tutor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import krllsv.tutor.api.dto.response.RaceConditionResult;
import krllsv.tutor.api.service.RaceConditionDemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/race-condition")
@RequiredArgsConstructor
@Tag(name = "Race Condition Demo", description = "Демонстрация race condition и его решения")
public class RaceConditionController {

    private final RaceConditionDemoService raceConditionDemoService;

    @GetMapping("/demo")
    @Operation(summary = "Демонстрация race condition и потокобезопасных решений")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Демонстрация выполнена"),
                            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    public ResponseEntity<RaceConditionResult> runRaceDemo(
            @RequestParam(defaultValue = "50") @Min(1) int threads,
            @RequestParam(defaultValue = "1000") @Min(1) int incrementsPerThread) throws InterruptedException {

        log.info("Запуск демонстрации race condition с {} потоками, {} инкрементов на поток",
                threads, incrementsPerThread);

        RaceConditionResult result = raceConditionDemoService.demonstrateRaceCondition(threads, incrementsPerThread);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reset")
    @Operation(summary = "Сбросить все счётчики")
    public ResponseEntity<String> resetCounters() {
        raceConditionDemoService.resetAllCounters();
        return ResponseEntity.ok("All counters reset successfully");
    }
}