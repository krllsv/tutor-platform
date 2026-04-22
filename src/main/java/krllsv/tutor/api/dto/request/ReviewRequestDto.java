package krllsv.tutor.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание/обновление отзыва")
public class ReviewRequestDto {
    @NotNull(message = "Оценка обязательна")
    @Min(value = 1, message = "Оценка должна быть от 1 до 5")
    @Max(value = 5, message = "Оценка должна быть от 1 до 5")
    @Schema(description = "Оценка от 1 до 5", example = "5")
    private Integer rating;
    @Schema(description = "Текст отзыва", example = "Отличный преподаватель!")
    private String comment;
    @NotNull(message = "ID студента обязателен")
    @Schema(description = "ID студента", example = "1")
    private Long studentId;
    @NotNull(message = "ID преподавателя обязателен")
    @Schema(description = "ID преподавателя", example = "1")
    private Long tutorId;
}
