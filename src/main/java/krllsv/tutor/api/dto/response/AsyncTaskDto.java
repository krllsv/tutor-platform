package krllsv.tutor.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация об асинхронной задаче")
public class AsyncTaskDto {
    @Schema(description = "ID задачи")
    private String taskId;
    @Schema(description = "Статус задачи")
    private String status;
    @Schema(description = "Тип операции")
    private String operationType;
    @Schema(description = "Прогресс выполнения (0-100)")
    private Integer progress;
    @Schema(description = "Сообщение о статусе")
    private String message;
    @Schema(description = "Время создания задачи")
    private LocalDateTime createdAt;
    @Schema(description = "Время завершения задачи")
    private LocalDateTime completedAt;
    @Schema(description = "Результат выполнения")
    private Object result;
    @Schema(description = "Ошибка выполнения")
    private String errorMessage;
}