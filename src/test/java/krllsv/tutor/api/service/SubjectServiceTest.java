package krllsv.tutor.api.service;

import krllsv.tutor.api.domain.Subject;
import krllsv.tutor.api.dto.request.SubjectRequestDto;
import krllsv.tutor.api.dto.response.SubjectResponseDto;
import krllsv.tutor.api.entity.SubjectEntity;
import krllsv.tutor.api.mapper.SubjectMapper;
import krllsv.tutor.api.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private SubjectService subjectService;

    private SubjectRequestDto requestDto;
    private SubjectEntity subjectEntity;
    private SubjectEntity savedSubjectEntity;
    private Subject domainSubject;
    private SubjectResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new SubjectRequestDto();
        requestDto.setName("Математика");
        requestDto.setCategory("Точные науки");
        requestDto.setDescription("Алгебра и геометрия");

        subjectEntity = new SubjectEntity();
        subjectEntity.setName("Математика");
        subjectEntity.setCategory("Точные науки");

        savedSubjectEntity = new SubjectEntity();
        savedSubjectEntity.setId(1L);
        savedSubjectEntity.setName("Математика");
        savedSubjectEntity.setCategory("Точные науки");

        domainSubject = new Subject();
        domainSubject.setId(1L);
        domainSubject.setName("Математика");
        domainSubject.setCategory("Точные науки");

        responseDto = new SubjectResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Математика");
        responseDto.setCategory("Точные науки");
    }

    @Test
    void createSubject_ShouldSaveSubject_WhenValidData() {
        when(subjectMapper.toEntity(requestDto)).thenReturn(subjectEntity);
        when(subjectRepository.save(any(SubjectEntity.class))).thenReturn(savedSubjectEntity);
        when(subjectMapper.toDomain(any(SubjectEntity.class))).thenReturn(domainSubject);
        when(subjectMapper.toResponseDto(any(Subject.class))).thenReturn(responseDto);

        SubjectResponseDto result = subjectService.createSubject(requestDto);

        assertNotNull(result);
        assertEquals("Математика", result.getName());
        verify(subjectRepository).save(any(SubjectEntity.class));
    }

    @Test
    void getSubjectById_ShouldReturnSubject_WhenExists() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(savedSubjectEntity));
        when(subjectMapper.toDomain(any(SubjectEntity.class))).thenReturn(domainSubject);
        when(subjectMapper.toResponseDto(any(Subject.class))).thenReturn(responseDto);

        SubjectResponseDto result = subjectService.getSubjectById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getSubjectById_ShouldThrowException_WhenNotFound() {
        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> subjectService.getSubjectById(999L));
    }

    @Test
    void deleteSubject_ShouldDelete_WhenExists() {
        when(subjectRepository.existsById(1L)).thenReturn(true);
        doNothing().when(subjectRepository).deleteById(1L);

        subjectService.deleteSubject(1L);

        verify(subjectRepository).deleteById(1L);
    }

    @Test
    void deleteSubject_ShouldThrowException_WhenNotFound() {
        when(subjectRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> subjectService.deleteSubject(999L));
        verify(subjectRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateSubject_ShouldUpdate_WhenExists() {
        Long id = 1L;
        when(subjectRepository.findById(id)).thenReturn(Optional.of(savedSubjectEntity));
        when(subjectRepository.save(any(SubjectEntity.class))).thenReturn(savedSubjectEntity);
        when(subjectMapper.toDomain(any(SubjectEntity.class))).thenReturn(domainSubject);
        when(subjectMapper.toResponseDto(any(Subject.class))).thenReturn(responseDto);

        SubjectResponseDto result = subjectService.updateSubject(id, requestDto);

        assertNotNull(result);
        verify(subjectRepository).save(any(SubjectEntity.class));
    }

    @Test
    void updateSubject_ShouldThrowException_WhenNotFound() {
        Long id = 999L;
        when(subjectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> subjectService.updateSubject(id, requestDto));
    }

    @Test
    void getAllSubjects_ShouldReturnList() {
        when(subjectRepository.findAll()).thenReturn(List.of(savedSubjectEntity));
        when(subjectMapper.toDomain(any(SubjectEntity.class))).thenReturn(domainSubject);
        when(subjectMapper.toResponseDto(any(Subject.class))).thenReturn(responseDto);

        List<SubjectResponseDto> result = subjectService.getAllSubjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(subjectRepository).findAll();
    }
}