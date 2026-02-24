package krllsv.tutor.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data               // Lombok: автоматически генерирует геттеры, сеттеры, конструкторы, методы equals(), hashCode() и toString()
@NoArgsConstructor  // Lombok: генерирует пустой конструктор
@AllArgsConstructor // Lombok: генерирует конструктор со всеми полями
public class Tutor {
    private Long id;
    private String firstName;
    private String lastName;
    private String subject;
    private BigDecimal hourlyRate; // Используется BigDecimal для точности при расчетах
    private int experienceYears;
    private Double Rating;
    private String email;
}
