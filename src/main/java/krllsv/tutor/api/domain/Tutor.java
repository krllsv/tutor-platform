package krllsv.tutor.api.domain;

import java.math.BigDecimal;
import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // Lombok: автоматически генерирует геттеры, сеттеры, ...
@NoArgsConstructor  // Lombok: генерирует пустой конструктор
@AllArgsConstructor // Lombok: генерирует конструктор со всеми полями
public class Tutor {
    private Long id;
    private String firstName;
    private String lastName;
    private BigDecimal hourlyRate; // Используется BigDecimal для точности при расчетах
    private int startYear;
    private String email;
    private Subject subject;

    public int getExperienceYears() {
        int currentYear = Year.now().getValue();
        return currentYear - startYear;
    }
}
