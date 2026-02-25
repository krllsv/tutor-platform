package krllsv.tutor.api.domain;

import java.math.BigDecimal;
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
    private String subject;
    private BigDecimal hourlyRate; // Используется BigDecimal для точности при расчетах
    private int experienceYears;
    private Double rating;
    private String email;
}
