package krllsv.tutor.api.mapper;

import krllsv.tutor.api.domain.Subject;
import krllsv.tutor.api.dto.request.SubjectRequestDto;
import krllsv.tutor.api.dto.response.SubjectResponseDto;
import krllsv.tutor.api.entity.SubjectEntity;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {
    public SubjectEntity toEntity(SubjectRequestDto dto) {
        if (dto == null) {
            return null;
        }

        SubjectEntity entity = new SubjectEntity();
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());

        return entity;
    }

    public Subject toDomain(SubjectEntity entity) {
        if (entity == null) {
            return null;
        }

        Subject subject = new Subject();
        subject.setId(entity.getId());
        subject.setName(entity.getName());
        subject.setCategory(entity.getCategory());
        subject.setDescription(entity.getDescription());

        return subject;
    }

    public SubjectResponseDto toResponseDto(Subject subject) {
        if (subject == null) {
            return null;
        }

        SubjectResponseDto dto = new SubjectResponseDto();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setCategory(subject.getCategory());
        dto.setDescription(subject.getDescription());

        return dto;
    }

    public SubjectResponseDto toResponseDto(SubjectEntity entity) {
        if (entity == null) {
            return null;
        }

        SubjectResponseDto dto = new SubjectResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setDescription(entity.getDescription());

        return dto;
    }
}
