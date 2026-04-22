package krllsv.tutor.api.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private StudentService studentService;

    private StudentRequestDto requestDto;
    private StudentEntity studentEntity;
    private StudentEntity savedStudentEntity;
    private SubjectEntity subjectEntity;
    private Student domainStudent;
    private StudentResponseDto responseDto;
    private SubjectResponseDto subjectResponseDto;

    @BeforeEach
    void setUp() {
        requestDto = new StudentRequestDto();
        requestDto.setFirstName("Иван");
        requestDto.setLastName("Петров");
        requestDto.setEmail("ivan@mail.com");
        requestDto.setPhone("+71234567890");
        requestDto.setBudget(BigDecimal.valueOf(5000));
        requestDto.setSubjectIds(List.of(1L, 2L));

        subjectEntity = new SubjectEntity();
        subjectEntity.setId(1L);
        subjectEntity.setName("Математика");

        studentEntity = new StudentEntity();
        studentEntity.setId(1L);
        studentEntity.setFirstName("Иван");
        studentEntity.setLastName("Петров");
        studentEntity.setEmail("ivan@mail.com");

        savedStudentEntity = new StudentEntity();
        savedStudentEntity.setId(1L);
        savedStudentEntity.setFirstName("Иван");
        savedStudentEntity.setLastName("Петров");
        savedStudentEntity.setEmail("ivan@mail.com");
        savedStudentEntity.setSubjects(new ArrayList<>());
        savedStudentEntity.getSubjects().add(subjectEntity);

        domainStudent = new Student();
        domainStudent.setId(1L);
        domainStudent.setFirstName("Иван");
        domainStudent.setLastName("Петров");

        responseDto = new StudentResponseDto();
        responseDto.setId(1L);
        responseDto.setFullName("Иван Петров");

        subjectResponseDto = new SubjectResponseDto();
        subjectResponseDto.setId(1L);
        subjectResponseDto.setName("Математика");
    }

    @Test
    void createStudent_ShouldSaveStudent_WhenValidData() {
        when(studentMapper.toEntity(requestDto)).thenReturn(studentEntity);
        when(subjectRepository.findAllById(requestDto.getSubjectIds())).thenReturn(List.of(subjectEntity));
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);
        when(subjectMapper.toResponseDto(any(SubjectEntity.class))).thenReturn(subjectResponseDto);

        StudentResponseDto result = studentService.createStudent(requestDto);

        assertNotNull(result);
        assertEquals("Иван Петров", result.getFullName());
        verify(studentRepository).save(any(StudentEntity.class));
    }

    @Test
    void createStudent_ShouldHandleNullSubjectIds() {
        StudentRequestDto nullSubjectRequest = new StudentRequestDto();
        nullSubjectRequest.setFirstName("Анна");
        nullSubjectRequest.setLastName("Сидорова");
        nullSubjectRequest.setEmail("anna@mail.com");
        nullSubjectRequest.setSubjectIds(null);

        when(studentMapper.toEntity(nullSubjectRequest)).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);

        StudentResponseDto result = studentService.createStudent(nullSubjectRequest);

        assertNotNull(result);
        verify(subjectRepository, never()).findAllById(any());
    }

    @Test
    void getStudentById_ShouldReturnStudent_WhenExists() {
        when(studentRepository.findByIdWithAll(1L)).thenReturn(Optional.of(savedStudentEntity));
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);
        when(subjectMapper.toResponseDto(any(SubjectEntity.class))).thenReturn(subjectResponseDto);

        StudentResponseDto result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getStudentById_ShouldThrowException_WhenNotFound() {
        when(studentRepository.findByIdWithAll(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentService.getStudentById(999L));
    }

    @Test
    void getAllStudents_ShouldReturnList() {
        when(studentRepository.findAllWithSubjects()).thenReturn(List.of(savedStudentEntity));
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);
        when(subjectMapper.toResponseDto(any(SubjectEntity.class))).thenReturn(subjectResponseDto);

        List<StudentResponseDto> result = studentService.getAllStudents();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepository).findAllWithSubjects();
    }

    @Test
    void updateStudent_ShouldUpdate_WhenExists() {
        Long id = 1L;
        StudentRequestDto updateRequest = new StudentRequestDto();
        updateRequest.setFirstName("Петр");
        updateRequest.setLastName("Иванов");
        updateRequest.setEmail("petr@mail.com");
        updateRequest.setPhone("+79876543210");
        updateRequest.setBudget(BigDecimal.valueOf(7000));
        updateRequest.setSubjectIds(List.of(2L));

        SubjectEntity newSubject = new SubjectEntity();
        newSubject.setId(2L);
        newSubject.setName("Физика");

        when(studentRepository.findById(id)).thenReturn(Optional.of(savedStudentEntity));
        when(subjectRepository.findAllById(updateRequest.getSubjectIds())).thenReturn(List.of(newSubject));
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);
        when(subjectMapper.toResponseDto(any(SubjectEntity.class))).thenReturn(subjectResponseDto);

        StudentResponseDto result = studentService.updateStudent(id, updateRequest);

        assertNotNull(result);
        verify(studentRepository).save(any(StudentEntity.class));
    }

    @Test
    void updateStudent_ShouldThrowException_WhenNotFound() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentService.updateStudent(999L, requestDto));
    }

    @Test
    void updateStudent_ShouldClearSubjects_WhenSubjectIdsIsNull() {
        Long id = 1L;
        StudentRequestDto updateRequest = new StudentRequestDto();
        updateRequest.setFirstName("Петр");
        updateRequest.setLastName("Иванов");
        updateRequest.setEmail("petr@mail.com");
        updateRequest.setSubjectIds(null);

        when(studentRepository.findById(id)).thenReturn(Optional.of(savedStudentEntity));
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);

        StudentResponseDto result = studentService.updateStudent(id, updateRequest);

        assertNotNull(result);
        verify(subjectRepository, never()).findAllById(any());
    }

    @Test
    void deleteStudent_ShouldDelete_WhenExists() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
    }

    @Test
    void deleteStudent_ShouldThrowException_WhenNotFound() {
        when(studentRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> studentService.deleteStudent(999L));
        verify(studentRepository, never()).deleteById(anyLong());
    }

    @Test
    void createWithoutTransaction_ShouldWork() {
        when(studentMapper.toEntity(requestDto)).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(subjectRepository.findAllById(requestDto.getSubjectIds())).thenReturn(List.of(subjectEntity));

        studentService.createWithoutTransaction(requestDto);

        verify(studentRepository, times(2)).save(any(StudentEntity.class));
    }

    @Test
    void createWithTransaction_ShouldWork() {
        when(studentMapper.toEntity(requestDto)).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(subjectRepository.findAllById(requestDto.getSubjectIds())).thenReturn(List.of(subjectEntity));

        studentService.createWithTransaction(requestDto);

        verify(studentRepository, times(2)).save(any(StudentEntity.class));
    }

    @Test
    void createStudentInternal_ShouldThrowException_WhenSecondSubjectFails() {
        StudentRequestDto twoSubjectsRequest = new StudentRequestDto();
        twoSubjectsRequest.setFirstName("Тест");
        twoSubjectsRequest.setLastName("Тестов");
        twoSubjectsRequest.setEmail("test@mail.com");
        twoSubjectsRequest.setSubjectIds(List.of(1L, 2L));

        when(studentMapper.toEntity(twoSubjectsRequest)).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(subjectRepository.findAllById(twoSubjectsRequest.getSubjectIds())).thenReturn(List.of(subjectEntity, subjectEntity));

        assertThrows(IllegalStateException.class, () -> studentService.createWithoutTransaction(twoSubjectsRequest));
        verify(studentRepository, times(3)).save(any(StudentEntity.class));
    }

    @Test
    void getAllStudentsWithProblem_ShouldReturnList() {
        when(studentRepository.findAll()).thenReturn(List.of(savedStudentEntity));
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);

        List<StudentResponseDto> result = studentService.getAllStudentsWithProblem();

        assertNotNull(result);
        verify(studentRepository).findAll();
    }

    @Test
    void buildFullResponse_ShouldHandleNullSubjects() {
        StudentEntity entityWithoutSubjects = new StudentEntity();
        entityWithoutSubjects.setId(2L);
        entityWithoutSubjects.setFirstName("Тест");
        entityWithoutSubjects.setLastName("Тестов");
        entityWithoutSubjects.setSubjects(null);

        when(studentRepository.findByIdWithAll(2L)).thenReturn(Optional.of(entityWithoutSubjects));
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);

        StudentResponseDto result = studentService.getStudentById(2L);

        assertNotNull(result);
        verify(subjectMapper, never()).toResponseDto(any(SubjectEntity.class));
    }

    @Test
    void createStudent_WithEmptySubjectIds_ShouldNotAddSubjects() {
        StudentRequestDto emptySubjectRequest = new StudentRequestDto();
        emptySubjectRequest.setFirstName("Анна");
        emptySubjectRequest.setLastName("Сидорова");
        emptySubjectRequest.setEmail("anna@mail.com");
        emptySubjectRequest.setSubjectIds(List.of());

        when(studentMapper.toEntity(emptySubjectRequest)).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);

        StudentResponseDto result = studentService.createStudent(emptySubjectRequest);

        assertNotNull(result);
        verify(subjectRepository, never()).findAllById(any());
    }

    @Test
    void updateStudent_WithEmptySubjectIds_ShouldCallFindAllById() {
        Long id = 1L;
        StudentRequestDto updateRequest = new StudentRequestDto();
        updateRequest.setFirstName("Петр");
        updateRequest.setLastName("Иванов");
        updateRequest.setEmail("petr@mail.com");
        updateRequest.setSubjectIds(List.of());

        when(studentRepository.findById(id)).thenReturn(Optional.of(savedStudentEntity));
        when(subjectRepository.findAllById(List.of())).thenReturn(List.of());
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);

        StudentResponseDto result = studentService.updateStudent(id, updateRequest);

        assertNotNull(result);
        verify(subjectRepository).findAllById(List.of());
    }

    @Test
    void buildFullResponse_WithEmptySubjects_ShouldNotSetSubjects() {
        StudentEntity entityWithEmptySubjects = new StudentEntity();
        entityWithEmptySubjects.setId(2L);
        entityWithEmptySubjects.setFirstName("Тест");
        entityWithEmptySubjects.setLastName("Тестов");
        entityWithEmptySubjects.setSubjects(List.of());

        when(studentRepository.findByIdWithAll(2L)).thenReturn(Optional.of(entityWithEmptySubjects));
        when(studentMapper.toDomain(any(StudentEntity.class))).thenReturn(domainStudent);
        when(studentMapper.toResponseDto(any(Student.class))).thenReturn(responseDto);

        StudentResponseDto result = studentService.getStudentById(2L);

        assertNotNull(result);
        verify(subjectMapper, never()).toResponseDto(any(SubjectEntity.class));
    }

    @Test
    void createStudentInternal_WithValidSubjects_ShouldCoverLine() {
        StudentRequestDto dto = new StudentRequestDto();
        dto.setFirstName("Тест");
        dto.setLastName("Тестов");
        dto.setEmail("test@mail.com");
        dto.setSubjectIds(List.of(1L));

        when(studentMapper.toEntity(dto)).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(subjectRepository.findAllById(dto.getSubjectIds())).thenReturn(List.of(subjectEntity));

        studentService.createWithoutTransaction(dto);

        verify(subjectRepository).findAllById(dto.getSubjectIds());
    }

    @Test
    void createStudentInternal_WithNullSubjectIds_ShouldNotEnterIfBlock() {
        StudentRequestDto dto = new StudentRequestDto();
        dto.setFirstName("Тест");
        dto.setLastName("Тестов");
        dto.setEmail("test@mail.com");
        dto.setSubjectIds(null);

        when(studentMapper.toEntity(dto)).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);

        studentService.createWithoutTransaction(dto);

        verify(subjectRepository, never()).findAllById(any());
    }

    @Test
    void createStudentInternal_WithEmptySubjectIds_ShouldNotEnterIfBlock() {
        StudentRequestDto dto = new StudentRequestDto();
        dto.setFirstName("Тест");
        dto.setLastName("Тестов");
        dto.setEmail("test@mail.com");
        dto.setSubjectIds(List.of());

        when(studentMapper.toEntity(dto)).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);

        studentService.createWithoutTransaction(dto);

        verify(subjectRepository, never()).findAllById(any());
    }

    @Test
    void createStudentInternal_WithNonEmptySubjectIds_ShouldEnterIfBlock() {
        StudentRequestDto dto = new StudentRequestDto();
        dto.setFirstName("Тест");
        dto.setLastName("Тестов");
        dto.setEmail("test@mail.com");
        dto.setSubjectIds(List.of(1L));

        when(studentMapper.toEntity(dto)).thenReturn(studentEntity);
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudentEntity);
        when(subjectRepository.findAllById(dto.getSubjectIds())).thenReturn(List.of(subjectEntity));

        studentService.createWithoutTransaction(dto);

        verify(subjectRepository, times(1)).findAllById(dto.getSubjectIds());
    }
}