package krllsv.tutor.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными бронирования")
public class BookingResponseDto {
    @Schema(description = "ID бронирования", example = "1")
    private Long id;
    @Schema(description = "Дата и время начала", example = "2024-03-20T15:00:00")
    private LocalDateTime dateTime;
    @Schema(description = "Длительность в минутах", example = "60")
    private int durationMinutes;
    @Schema(description = "Время окончания", example = "2024-03-20T16:00:00")
    private LocalDateTime endTime;
    @Schema(description = "Статус бронирования", example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"})
    private String status;
    @Schema(description = "Сообщение", example = "Хочу разобрать домашнее задание")
    private String message;
    @Schema(description = "ID студента", example = "1")
    private Long studentId;
    @Schema(description = "Имя студента", example = "Иван Петров")
    private String studentName;
    @Schema(description = "ID преподавателя", example = "1")
    private Long tutorId;
    @Schema(description = "Имя преподавателя", example = "Мария Иванова")
    private String tutorName;
}