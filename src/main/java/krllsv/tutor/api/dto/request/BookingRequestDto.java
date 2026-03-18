package krllsv.tutor.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    private LocalDateTime dateTime;
    private int durationMinutes;
    private String message;
    private Long studentId;
    private Long tutorId;
}
