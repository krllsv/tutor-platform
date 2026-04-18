package krllsv.tutor.api.service;

import krllsv.tutor.api.cache.QueryCache;
import krllsv.tutor.api.dto.request.TutorRequestDto;
import krllsv.tutor.api.dto.response.TutorResponseDto;
import krllsv.tutor.api.entity.SubjectEntity;
import krllsv.tutor.api.entity.TutorEntity;
import krllsv.tutor.api.mapper.TutorMapper;
import krllsv.tutor.api.repository.SubjectRepository;
import krllsv.tutor.api.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {
    @Mock
    private TutorRepository tutorRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private TutorMapper tutorMapper;
    @Mock
    private QueryCache queryCache;
    @InjectMocks
    private TutorService tutorService;

    private TutorRequestDto requestDto;
    private TutorEntity tutorEntity;
    private SubjectEntity subjectEntity;
    private TutorResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new TutorRequestDto();
        requestDto.setFirstName("Иван");
        requestDto.setLastName("Петров");
        requestDto.setEmail("ivan@mail.com");
        requestDto.setHourlyRate(BigDecimal.valueOf(1500));
        requestDto.setStartYear(2018);
        requestDto.setSubjectId(1L);

        tutorEntity = new TutorEntity();
        tutorEntity.setId(1L);
        tutorEntity.setFirstName("Иван");
        tutorEntity.setLastName("Петров");
        tutorEntity.setEmail("ivan@mail.com");

        subjectEntity = new SubjectEntity();
        subjectEntity.setId(1L);
        subjectEntity.setName("Математика");

        responseDto = new TutorResponseDto();
        responseDto.setId(1L);
        responseDto.setFullname("Иван Петров");
    }

    @Test
    void createTutor_ShouldSaveTutor_WhenValidData() {
        when(tutorRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectEntity));
        when(tutorMapper.toEntity(requestDto)).thenReturn(tutorEntity);
        when(tutorRepository.save(any(TutorEntity.class))).thenReturn(tutorEntity);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(null);
        when(tutorMapper.toResponseDto(any())).thenReturn(responseDto);

        TutorResponseDto result = tutorService.createTutor(requestDto);

        assertNotNull(result);
        assertEquals("Иван Петров", result.getFullname());
        verify(tutorRepository).save(any(TutorEntity.class));
    }

    @Test
    void createTutor_ShouldThrowException_WhenEmailAlreadyExists() {
        when(tutorRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> tutorService.createTutor(requestDto));
        verify(tutorRepository, never()).save(any(TutorEntity.class));
    }

    @Test
    void createTutor_ShouldThrowException_WhenSubjectNotFound() {
        when(tutorRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tutorService.createTutor(requestDto));
        verify(tutorRepository, never()).save(any(TutorEntity.class));
    }

    @Test
    void getTutorById_ShouldReturnTutor_WhenExists() {
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorEntity));
        when(tutorMapper.toDomain(tutorEntity)).thenReturn(null);
        when(tutorMapper.toResponseDto(any())).thenReturn(responseDto);

        TutorResponseDto result = tutorService.getTutorById(1L);

        assertNotNull(result);
        assertEquals("Иван Петров", result.getFullname());
    }

    @Test
    void getTutorById_ShouldThrowException_WhenNotFound() {
        when(tutorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tutorService.getTutorById(999L));
    }

    @Test
    void deleteTutor_ShouldDelete_WhenExists() {
        when(tutorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tutorRepository).deleteById(1L);

        tutorService.deleteTutor(1L);

        verify(tutorRepository).deleteById(1L);
    }

    @Test
    void deleteTutor_ShouldThrowException_WhenNotFound() {
        when(tutorRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> tutorService.deleteTutor(999L));
        verify(tutorRepository, never()).deleteById(anyLong());
    }

    @Test
    void createTutorsBulk_ShouldCreateAll_WhenNoErrors() {
        when(tutorRepository.existsByEmail(anyString())).thenReturn(false);
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.of(subjectEntity));
        when(tutorMapper.toEntity(any(TutorRequestDto.class))).thenReturn(tutorEntity);
        when(tutorRepository.save(any(TutorEntity.class))).thenReturn(tutorEntity);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(null);
        when(tutorMapper.toResponseDto(any())).thenReturn(responseDto);

        List<TutorRequestDto> dtos = List.of(requestDto, requestDto);
        List<TutorResponseDto> result = tutorService.createTutorsBulk(dtos);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tutorRepository, times(2)).save(any(TutorEntity.class));
    }

    @Test
    void getTutorsSortedByHourlyRate_ShouldReturnSortedList() {
        TutorEntity tutor1 = new TutorEntity();
        tutor1.setHourlyRate(BigDecimal.valueOf(1000));
        TutorEntity tutor2 = new TutorEntity();
        tutor2.setHourlyRate(BigDecimal.valueOf(2000));

        when(tutorRepository.findAll()).thenReturn(List.of(tutor2, tutor1));
        when(tutorMapper.toDomain(any())).thenReturn(null);
        when(tutorMapper.toResponseDto(any())).thenReturn(responseDto);

        List<TutorResponseDto> result = tutorService.getTutorsSortedByHourlyRate();

        assertNotNull(result);
        verify(tutorRepository).findAll();
    }
}