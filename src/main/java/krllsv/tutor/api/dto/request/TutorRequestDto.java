package krllsv.tutor.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание/обновление преподавателя")
public class TutorRequestDto {
    @NotBlank(message = " Имя обязательно")
    @Size(min = 2, max = 50, message = " Имя должно быть от 2 до 50 символов")
    @Schema(description = "Имя преподавателя", example = "Иван")
    private String firstName;
    @NotBlank(message = " Фамилия обязательна")
    @Size(min = 2, max = 50, message = " Фамилия должна быть от 2 до 50 символов")
    @Schema(description = "Фамилия преподавателя", example = "Петров")
    private String lastName;
    @NotNull(message = " Почасовая ставка обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = " Ставка должна быть больше 0")
    @Schema(description = "Почасовая ставка", example = "1500.00")
    private BigDecimal hourlyRate;
    @NotNull(message = " Год начала работы обязателен")
    @Min(value = 1900, message = " Год не может быть раньше 1900")
    @Max(value = 2026, message = " Год не может быть в будущем")
    @Schema(description = "Год начала работы", example = "2018")
    private int startYear;
    @NotBlank(message = " Email обязателен")
    @Email(message = " Неверный формат email")
    @Schema(description = "Email", example = "ivan@mail.com")
    private String email;
    @Schema(description = "ID предмета", example = "1")
    private Long subjectId;
}