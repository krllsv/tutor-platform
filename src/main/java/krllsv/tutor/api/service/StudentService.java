package krllsv.tutor.api.service;

import jakarta.persistence.EntityNotFoundException;
import krllsv.tutor.api.domain.Student;
import krllsv.tutor.api.dto.request.StudentRequestDto;
import krllsv.tutor.api.dto.response.StudentResponseDto;
import krllsv.tutor.api.dto.response.SubjectResponseDto;
import krllsv.tutor.api.entity.StudentEntity;
import krllsv.tutor.api.entity.SubjectEntity;
import krllsv.tutor.api.mapper.StudentMapper;
import krllsv.tutor.api.mapper.SubjectMapper;
import krllsv.tutor.api.repository.StudentRepository;
import krllsv.tutor.api.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentMapper studentMapper;
    private final SubjectMapper subjectMapper;

    public StudentService(StudentRepository studentRepository,
                          SubjectRepository subjectRepository,
                          StudentMapper studentMapper,
                          SubjectMapper subjectMapper
    ) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.studentMapper = studentMapper;
        this.subjectMapper = subjectMapper;
    }

    public void createWithoutTransaction(StudentRequestDto requestDto) {
        createStudentInternal(requestDto);
    }

    @Transactional
    public void createWithTransaction(StudentRequestDto requestDto) {
        createStudentInternal(requestDto);
    }

    private void createStudentInternal(StudentRequestDto requestDto) {
        StudentEntity student = studentMapper.toEntity(requestDto);
        StudentEntity savedStudent = studentRepository.save(student);

        if (requestDto.getSubjectIds() != null && !requestDto.getSubjectIds().isEmpty()) {
            List<SubjectEntity> subjects = subjectRepository.findAllById(requestDto.getSubjectIds());

            int count = 0;
            for (SubjectEntity subject : subjects) {
                count++;
                savedStudent.getSubjects().add(subject);
                studentRepository.save(savedStudent);

                if (count == 2) {
                    throw new IllegalStateException("Ошибка при привязке 2-го предмета");
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAllStudentsWithProblem() {
        List<StudentEntity> students = studentRepository.findAll();
        return students.stream()
                .map(studentMapper::toDomain)
                .map(studentMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public StudentResponseDto createStudent(StudentRequestDto requestDto) {
        StudentEntity entity = studentMapper.toEntity(requestDto);

        if (requestDto.getSubjectIds() != null && !requestDto.getSubjectIds().isEmpty()) {
            List<SubjectEntity> subjects = subjectRepository.findAllById(requestDto.getSubjectIds());
            entity.getSubjects().addAll(subjects);
        }

        StudentEntity savedEntity = studentRepository.save(entity);

        return buildFullResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAllStudents() {
        List<StudentEntity> students = studentRepository.findAllWithSubjects();
        return students.stream()
                .map(this::buildFullResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(Long id) {
        StudentEntity entity = studentRepository.findByIdWithAll(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        return buildFullResponse(entity);
    }

    @Transactional
    public StudentResponseDto updateStudent(Long id, StudentRequestDto requestDto) {
        StudentEntity existingEntity = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        existingEntity.setFirstName(requestDto.getFirstName());
        existingEntity.setLastName(requestDto.getLastName());
        existingEntity.setPhone(requestDto.getPhone());
        existingEntity.setEmail(requestDto.getEmail());
        existingEntity.setBudget(requestDto.getBudget());

        if (requestDto.getSubjectIds() != null) {
            existingEntity.getSubjects().clear();
            List<SubjectEntity> newSubjects = subjectRepository.findAllById(requestDto.getSubjectIds());
            existingEntity.getSubjects().addAll(newSubjects);
        }

        StudentEntity updatedEntity = studentRepository.save(existingEntity);
        return buildFullResponse(updatedEntity);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    private StudentResponseDto buildFullResponse(StudentEntity entity) {
        Student student = studentMapper.toDomain(entity);
        StudentResponseDto dto = studentMapper.toResponseDto(student);

        if (entity.getSubjects() != null && !entity.getSubjects().isEmpty()) {
            List<SubjectResponseDto> subjectDtos = entity.getSubjects().stream()
                    .map(subjectMapper::toResponseDto)
                    .collect(Collectors.toList());
            dto.setSubjects(subjectDtos);
        }

        return dto;
    }
}
