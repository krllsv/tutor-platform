package krllsv.tutor.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание/обновление бронирования")
public class BookingRequestDto {
    @NotNull(message = "Дата и время обязательны")
    @Future(message = "Дата и время должны быть в будущем")
    @Schema(description = "Дата и время начала занятия", example = "2024-03-20T15:00:00", required = true)
    private LocalDateTime dateTime;
    @Min(value = 30, message = "Минимальная длительность 30 минут")
    @Schema(description = "Длительность в минутах", example = "60", required = true)
    private int durationMinutes;
    @Schema(description = "Сообщение от студента", example = "Хочу разобрать домашнее задание")
    private String message;
    @NotNull(message = "ID студента обязателен")
    @Schema(description = "ID студента", example = "1", required = true)
    private Long studentId;
    @NotNull(message = "ID преподавателя обязателен")
    @Schema(description = "ID преподавателя", example = "1", required = true)
    private Long tutorId;
}
