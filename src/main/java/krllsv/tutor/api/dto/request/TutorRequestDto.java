package krllsv.tutor.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorRequestDto {
    private String firstName;
    private String lastName;
    private BigDecimal hourlyRate;
    private int startYear;
    private String email;
    private Long subjectId;
}