package krllsv.tutor.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными преподавателя")
public class TutorResponseDto {
    @Schema(description = "ID преподавателя", example = "1")
    private Long id;
    @Schema(description = "Полное имя", example = "Иван Петров")
    private String fullname;
    @Schema(description = "Почасовая ставка", example = "1500.00")
    private BigDecimal hourlyRate;
    @Schema(description = "Опыт работы в годах", example = "8")
    private int experienceYears;
    @Schema(description = "ID предмета", example = "1")
    private Long subjectId;
    @Schema(description = "Название предмета", example = "Математика")
    private String subjectName;
}
