package krllsv.tutor.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными студента")
public class StudentResponseDto {
    @Schema(description = "ID студента", example = "1")
    private Long id;
    @Schema(description = "Полное имя", example = "Иван Петров")
    private String fullName;
    @Schema(description = "Телефон", example = "+71234567890")
    private String phone;
    @Schema(description = "Email", example = "ivan@gmail.com")
    private String email;
    @Schema(description = "Бюджет", example = "5000")
    private BigDecimal budget;

    @Schema(description = "Изучаемые предметы")
    private List<SubjectResponseDto> subjects;
}