package krllsv.tutor.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private BigDecimal budget;

    private List<Long> subjectIds = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
