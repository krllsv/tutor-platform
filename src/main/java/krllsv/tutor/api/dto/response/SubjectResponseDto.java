package krllsv.tutor.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponseDto {
    private Long id;
    private String name;
    private String category;
    private String description;
}
