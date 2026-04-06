package krllsv.tutor.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными отзыва")
public class ReviewResponseDto {
    @Schema(description = "ID отзыва", example = "1")
    private Long id;
    @Schema(description = "Оценка", example = "5")
    private Integer rating;
    @Schema(description = "Текст отзыва", example = "Отличный преподаватель!")
    private String comment;
    @Schema(description = "Дата создания", example = "2024-03-20T15:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "ID студента", example = "1")
    private Long studentId;
    @Schema(description = "Имя студента", example = "Иван Петров")
    private String studentName;
    @Schema(description = "ID преподавателя", example = "1")
    private Long tutorId;
    @Schema(description = "Имя преподавателя", example = "Мария Иванова")
    private String tutorName;
}