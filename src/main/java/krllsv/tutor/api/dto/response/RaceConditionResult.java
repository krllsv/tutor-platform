package krllsv.tutor.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Результат демонстрации race condition")
public class RaceConditionResult {

    @Schema(description = "Количество потоков")
    private int threadsCount;

    @Schema(description = "Количество инкрементов на поток")
    private int incrementsPerThread;

    @Schema(description = "Ожидаемое значение")
    private long expectedValue;

    @Schema(description = "Небезопасный счётчик (с проблемой race condition)")
    private long unsafeCounter;

    @Schema(description = "Безопасный счётчик через synchronized")
    private long syncCounter;

    @Schema(description = "Безопасный счётчик через AtomicLong")
    private long atomicCounter;

    @Schema(description = "Количество потерянных инкрементов")
    private long lostValues;

    @Schema(description = "Процент потерянных значений")
    private double lostPercentage;

    @Schema(description = "Вывод/заключение")
    private String conclusion;

    @Schema(description = "Время выполнения в мс")
    private long executionTimeMs;
}