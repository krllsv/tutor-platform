package krllsv.tutor.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorResponseDto {
    private Long id;
    private String fullname;
    private String subject;
    private BigDecimal hourlyRate;
    private int experienceYears;
    private Double rating;
}
