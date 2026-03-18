package krllsv.tutor.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDto {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private BigDecimal budget;

    private List<SubjectResponseDto> subjects;
}