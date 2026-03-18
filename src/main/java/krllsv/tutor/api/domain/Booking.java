package krllsv.tutor.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDateTime dateTime;
    private int durationMinutes;
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private String message;

    private Long studentId;
    private Long tutorId;

    public LocalDateTime getEndTime() {
        return dateTime.plusMinutes(durationMinutes);
    }
}
