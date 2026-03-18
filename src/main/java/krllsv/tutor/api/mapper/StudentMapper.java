package krllsv.tutor.api.mapper;

import krllsv.tutor.api.domain.Student;
import krllsv.tutor.api.dto.request.StudentRequestDto;
import krllsv.tutor.api.dto.response.StudentResponseDto;
import krllsv.tutor.api.entity.StudentEntity;
import krllsv.tutor.api.entity.SubjectEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class StudentMapper {
    private final SubjectMapper subjectMapper;

    public StudentMapper(SubjectMapper subjectMapper) {
        this.subjectMapper = subjectMapper;
    }

    public StudentEntity toEntity(StudentRequestDto dto) {
        if (dto == null) {
            return null;
        }

        StudentEntity entity = new StudentEntity();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setBudget(dto.getBudget());

        return entity;
    }

    public Student toDomain(StudentEntity entity) {
        if (entity == null) {
            return null;
        }

        Student student = new Student();
        student.setId(entity.getId());
        student.setFirstName(entity.getFirstName());
        student.setLastName(entity.getLastName());
        student.setPhone(entity.getPhone());
        student.setEmail(entity.getEmail());
        student.setBudget(entity.getBudget());

        if (entity.getSubjects() != null) {
            student.setSubjectIds(entity.getSubjects().stream()
                    .map(SubjectEntity::getId)
                    .collect(Collectors.toList()));
        }

        return student;
    }

    public StudentResponseDto toResponseDto(Student student) {
        if (student == null) {
            return null;
        }

        StudentResponseDto dto = new StudentResponseDto();
        dto.setId(student.getId());
        dto.setFullName(student.getFirstName() + " " + student.getLastName());
        dto.setPhone(student.getPhone());
        dto.setEmail(student.getEmail());
        dto.setBudget(student.getBudget());

        return dto;
    }
}
