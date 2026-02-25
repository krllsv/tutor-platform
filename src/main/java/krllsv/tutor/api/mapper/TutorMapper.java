package krllsv.tutor.api.mapper;

import krllsv.tutor.api.domain.Tutor;
import krllsv.tutor.api.dto.TutorResponseDto;
import org.springframework.stereotype.Component;

@Component
public class TutorMapper {

    public TutorResponseDto toDto(Tutor tutor) {
        if (tutor == null) {
            return null;
        }

        TutorResponseDto dto = new TutorResponseDto();
        dto.setId(tutor.getId());
        dto.setFullname(tutor.getFirstName() + " " + tutor.getLastName());
        dto.setSubject(tutor.getSubject());
        dto.setHourlyRate(tutor.getHourlyRate());
        dto.setExperienceYears(tutor.getExperienceYears());
        dto.setRating(tutor.getRating());

        return dto;
    }
}
