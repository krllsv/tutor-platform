package krllsv.tutor.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными предмета")
public class SubjectResponseDto {
    @Schema(description = "ID предмета", example = "1")
    private Long id;
    @Schema(description = "Название предмета", example = "Математика")
    private String name;
    @Schema(description = "Категория", example = "Точные науки")
    private String category;
    @Schema(description = "Описание", example = "Алгебра, геометрия, тригонометрия")
    private String description;
}
