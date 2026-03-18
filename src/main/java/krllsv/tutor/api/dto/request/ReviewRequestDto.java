package krllsv.tutor.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    private int rating;
    private String comment;
    private Long studentId;
    private Long tutorId;
}
