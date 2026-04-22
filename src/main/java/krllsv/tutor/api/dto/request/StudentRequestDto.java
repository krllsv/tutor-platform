package krllsv.tutor.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание/обновление студента")
public class StudentRequestDto {
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Schema(description = "Имя студента", example = "Иван")
    private String firstName;
    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Schema(description = "Фамилия студента", example = "Петров")
    private String lastName;
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,20}$", message = "Неверный формат телефона")
    @Schema(description = "Номер телефона", example = "+71234567890")
    private String phone;
    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    @Schema(description = "Email", example = "ivan@mail.com")
    private String email;
    @DecimalMin(value = "0.0", message = "Бюджет не может быть отрицательным")
    @Schema(description = "Бюджет", example = "5000")
    private BigDecimal budget;

    @Schema(description = "ID предметов, которые изучает студент", example = "[1, 2, 3]")
    private List<Long> subjectIds;
}