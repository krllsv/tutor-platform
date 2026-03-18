package krllsv.tutor.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDto {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private BigDecimal budget;

    private List<Long> subjectIds;
}