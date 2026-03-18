package krllsv.tutor.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    private Long studentId;
    private String studentName;

    private Long tutorId;
    private String tutorName;
}
