package krllsv.tutor.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;
    private LocalDateTime dateTime;
    private int durationMinutes;
    private LocalDateTime endTime;
    private String status;
    private String message;

    private Long studentId;
    private String studentName;

    private Long tutorId;
    private String tutorName;
}