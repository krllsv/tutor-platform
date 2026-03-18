package krllsv.tutor.api.mapper;

import krllsv.tutor.api.domain.Subject;
import krllsv.tutor.api.domain.Tutor;
import krllsv.tutor.api.dto.request.TutorRequestDto;
import krllsv.tutor.api.dto.response.TutorResponseDto;
import krllsv.tutor.api.entity.TutorEntity;
import org.springframework.stereotype.Component;

@Component
public class TutorMapper {
    private final SubjectMapper subjectMapper;

    public TutorMapper(SubjectMapper subjectMapper) {
        this.subjectMapper = subjectMapper;
    }

    public TutorEntity toEntity(TutorRequestDto dto) {
        if (dto == null) {
            return null;
        }

        TutorEntity entity = new TutorEntity();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setHourlyRate(dto.getHourlyRate());
        entity.setStartYear(dto.getStartYear());
        entity.setEmail(dto.getEmail());
        return entity;
    }

    public Tutor toDomain(TutorEntity tutorEntity) {
        if (tutorEntity == null) {
            return null;
        }

        Tutor tutor = new Tutor();
        tutor.setId(tutorEntity.getId());
        tutor.setFirstName(tutorEntity.getFirstName());
        tutor.setLastName(tutorEntity.getLastName());
        tutor.setHourlyRate(tutorEntity.getHourlyRate());
        tutor.setStartYear(tutorEntity.getStartYear());
        tutor.setEmail(tutorEntity.getEmail());
        if (tutorEntity.getSubject() != null) {
            Subject subject = subjectMapper.toDomain(tutorEntity.getSubject());  // нужен subjectMapper
            tutor.setSubject(subject);
        }
        return tutor;
    }

    public TutorResponseDto toResponseDto(Tutor tutor) {
        if (tutor == null) {
            return null;
        }

        TutorResponseDto dto = new TutorResponseDto();
        dto.setId(tutor.getId());
        dto.setFullname(tutor.getFirstName() + " " + tutor.getLastName());
        dto.setHourlyRate(tutor.getHourlyRate());
        dto.setExperienceYears(tutor.getExperienceYears());
        if (tutor.getSubject() != null) {
            dto.setSubjectId(tutor.getSubject().getId());
            dto.setSubjectName(tutor.getSubject().getName());
        }
        return dto;
    }
}