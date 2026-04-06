package krllsv.tutor.api.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на создание/обновление предмета")
public class SubjectRequestDto {
    @NotBlank(message = "Название предмета обязательно")
    @Size(min = 2, max = 100, message = "Название должно быть от 2 до 100 символов")
    @Schema(description = "Название предмета", example = "Математика", required = true)
    private String name;
    @Schema(description = "Категория предмета", example = "Точные науки")
    private String category;
    @Schema(description = "Описание предмета", example = "Алгебра, геометрия, тригонометрия")
    private String description;
}
