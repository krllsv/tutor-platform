package krllsv.tutor.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Массовое создание преподавателей")
public class TutorBulkRequestDto {

    @NotNull(message = "Список преподавателей не может быть пустым")
    @Size(min = 1, max = 50, message = "Количество преподавателей должно быть от 0 до 50")
    @Valid
    @Schema(description = "Список преподавателей для создания")
    private List<TutorRequestDto> tutors;
}