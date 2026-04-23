package krllsv.tutor.api.service;

import krllsv.tutor.api.cache.QueryCache;
import krllsv.tutor.api.domain.Tutor;
import krllsv.tutor.api.dto.request.TutorRequestDto;
import krllsv.tutor.api.dto.response.TutorResponseDto;
import krllsv.tutor.api.entity.ReviewEntity;
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
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private TutorEntity savedTutorEntity;
    private SubjectEntity subjectEntity;
    private Tutor domainTutor;
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

        subjectEntity = new SubjectEntity();
        subjectEntity.setId(1L);
        subjectEntity.setName("Математика");

        tutorEntity = new TutorEntity();
        tutorEntity.setId(1L);
        tutorEntity.setFirstName("Иван");
        tutorEntity.setLastName("Петров");
        tutorEntity.setEmail("ivan@mail.com");
        tutorEntity.setHourlyRate(BigDecimal.valueOf(1500));
        tutorEntity.setStartYear(2018);

        savedTutorEntity = new TutorEntity();
        savedTutorEntity.setId(1L);
        savedTutorEntity.setFirstName("Иван");
        savedTutorEntity.setLastName("Петров");
        savedTutorEntity.setEmail("ivan@mail.com");
        savedTutorEntity.setHourlyRate(BigDecimal.valueOf(1500));
        savedTutorEntity.setStartYear(2018);
        savedTutorEntity.setSubject(subjectEntity);

        domainTutor = new Tutor();
        domainTutor.setId(1L);
        domainTutor.setFirstName("Иван");
        domainTutor.setLastName("Петров");
        domainTutor.setEmail("ivan@mail.com");

        responseDto = new TutorResponseDto();
        responseDto.setId(1L);
        responseDto.setFullname("Иван Петров");
        responseDto.setHourlyRate(BigDecimal.valueOf(1500));
        responseDto.setExperienceYears(6);
    }

    @Test
    void createTutor_ShouldSaveTutor_WhenValidData() {
        when(tutorRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectEntity));
        when(tutorMapper.toEntity(requestDto)).thenReturn(tutorEntity);
        when(tutorRepository.save(any(TutorEntity.class))).thenReturn(savedTutorEntity);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        TutorResponseDto result = tutorService.createTutor(requestDto);

        assertNotNull(result);
        assertEquals("Иван Петров", result.getFullname());
        verify(tutorRepository).save(any(TutorEntity.class));
        verify(queryCache).invalidateByEndpoint("by-subject");
        verify(queryCache).invalidateByEndpoint("by-subject-native");
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
    void createTutor_ShouldHandleNullSubjectId() {
        TutorRequestDto requestWithoutSubject = new TutorRequestDto();
        requestWithoutSubject.setFirstName("Анна");
        requestWithoutSubject.setLastName("Сидорова");
        requestWithoutSubject.setEmail("anna@mail.com");
        requestWithoutSubject.setHourlyRate(BigDecimal.valueOf(1500));
        requestWithoutSubject.setStartYear(2018);
        requestWithoutSubject.setSubjectId(null);

        when(tutorRepository.existsByEmail(anyString())).thenReturn(false);
        when(tutorMapper.toEntity(requestWithoutSubject)).thenReturn(tutorEntity);
        when(tutorRepository.save(any(TutorEntity.class))).thenReturn(savedTutorEntity);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        TutorResponseDto result = tutorService.createTutor(requestWithoutSubject);

        assertNotNull(result);
        verify(subjectRepository, never()).findById(anyLong());
    }

    @Test
    void getTutorById_ShouldReturnTutor_WhenExists() {
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(savedTutorEntity));
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        Optional<TutorResponseDto> result = tutorService.getTutorById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getTutorById_ShouldThrowException_WhenNotFound() {
        when(tutorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tutorService.getTutorById(999L));
    }

    @Test
    void getAllTutors_ShouldReturnList() {
        when(tutorRepository.findAllWithSubject()).thenReturn(List.of(savedTutorEntity));
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        List<TutorResponseDto> result = tutorService.getAllTutors();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tutorRepository).findAllWithSubject();
    }

    @Test
    void updateTutor_ShouldUpdate_WhenExists() {
        Long id = 1L;
        TutorRequestDto updateRequest = new TutorRequestDto();
        updateRequest.setFirstName("Петр");
        updateRequest.setLastName("Иванов");
        updateRequest.setEmail("petr@mail.com");
        updateRequest.setHourlyRate(BigDecimal.valueOf(2000));
        updateRequest.setStartYear(2015);
        updateRequest.setSubjectId(1L);

        when(tutorRepository.findById(id)).thenReturn(Optional.of(savedTutorEntity));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectEntity));
        when(tutorRepository.save(any(TutorEntity.class))).thenReturn(savedTutorEntity);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        TutorResponseDto result = tutorService.updateTutor(id, updateRequest);

        assertNotNull(result);
        verify(tutorRepository).save(any(TutorEntity.class));
        verify(queryCache).invalidateByEndpoint("by-subject");
        verify(queryCache).invalidateByEndpoint("by-subject-native");
    }

    @Test
    void updateTutor_ShouldThrowException_WhenNotFound() {
        Long id = 999L;
        when(tutorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tutorService.updateTutor(id, requestDto));
    }

    @Test
    void updateTutor_ShouldRemoveSubject_WhenSubjectIdIsNull() {
        Long id = 1L;
        TutorRequestDto updateRequest = new TutorRequestDto();
        updateRequest.setFirstName("Петр");
        updateRequest.setLastName("Иванов");
        updateRequest.setEmail("petr@mail.com");
        updateRequest.setHourlyRate(BigDecimal.valueOf(2000));
        updateRequest.setStartYear(2015);
        updateRequest.setSubjectId(null);

        when(tutorRepository.findById(id)).thenReturn(Optional.of(savedTutorEntity));
        when(tutorRepository.save(any(TutorEntity.class))).thenReturn(savedTutorEntity);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        TutorResponseDto result = tutorService.updateTutor(id, updateRequest);

        assertNotNull(result);
        verify(subjectRepository, never()).findById(anyLong());
        verify(tutorRepository).save(any(TutorEntity.class));
    }

    @Test
    void updateTutor_ShouldUpdateSubject_WhenSubjectIdChanged() {
        Long id = 1L;
        TutorRequestDto updateRequest = new TutorRequestDto();
        updateRequest.setFirstName("Петр");
        updateRequest.setLastName("Иванов");
        updateRequest.setEmail("petr@mail.com");
        updateRequest.setHourlyRate(BigDecimal.valueOf(2000));
        updateRequest.setStartYear(2015);
        updateRequest.setSubjectId(2L);

        SubjectEntity newSubject = new SubjectEntity();
        newSubject.setId(2L);
        newSubject.setName("Физика");

        when(tutorRepository.findById(id)).thenReturn(Optional.of(savedTutorEntity));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(newSubject));
        when(tutorRepository.save(any(TutorEntity.class))).thenReturn(savedTutorEntity);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        TutorResponseDto result = tutorService.updateTutor(id, updateRequest);

        assertNotNull(result);
        verify(subjectRepository).findById(2L);
    }

    @Test
    void deleteTutor_ShouldDelete_WhenExists() {
        when(tutorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tutorRepository).deleteById(1L);

        tutorService.deleteTutor(1L);

        verify(tutorRepository).deleteById(1L);
        verify(queryCache).invalidateByEndpoint("by-subject");
        verify(queryCache).invalidateByEndpoint("by-subject-native");
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
        when(tutorRepository.save(any(TutorEntity.class))).thenReturn(savedTutorEntity);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        List<TutorRequestDto> dtos = List.of(requestDto, requestDto);
        List<TutorResponseDto> result = tutorService.createTutorsBulk(dtos);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tutorRepository, times(2)).save(any(TutorEntity.class));
    }

    @Test
    void createTutorsBulkWithoutTransaction_ShouldCreateAll() {
        when(tutorRepository.existsByEmail(anyString())).thenReturn(false);
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.of(subjectEntity));
        when(tutorMapper.toEntity(any(TutorRequestDto.class))).thenReturn(tutorEntity);
        when(tutorRepository.save(any(TutorEntity.class))).thenReturn(savedTutorEntity);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        List<TutorRequestDto> dtos = List.of(requestDto, requestDto);
        List<TutorResponseDto> result = tutorService.createTutorsBulkWithoutTransaction(dtos);

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
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        List<TutorResponseDto> result = tutorService.getTutorsSortedByHourlyRate();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tutorRepository).findAll();
    }

    @Test
    void getTutorsByMinRating_ShouldReturnFilteredList() {
        ReviewEntity review1 = new ReviewEntity();
        review1.setRating(5);
        ReviewEntity review2 = new ReviewEntity();
        review2.setRating(4);

        TutorEntity tutor1 = new TutorEntity();
        tutor1.setReviews(List.of(review1, review2));
        TutorEntity tutor2 = new TutorEntity();
        tutor2.setReviews(List.of(review2));

        when(tutorRepository.findAll()).thenReturn(List.of(tutor1, tutor2));
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);

        List<TutorResponseDto> result = tutorService.getTutorsByMinRating(4.5);

        assertNotNull(result);
        verify(tutorRepository).findAll();
    }

    @Test
    void getTutorsByMinRating_ShouldReturnEmptyList_WhenNoMatches() {
        ReviewEntity review = new ReviewEntity();
        review.setRating(3);

        TutorEntity tutor = new TutorEntity();
        tutor.setReviews(List.of(review));

        when(tutorRepository.findAll()).thenReturn(List.of(tutor));

        List<TutorResponseDto> result = tutorService.getTutorsByMinRating(4.5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tutorRepository).findAll();
    }

    @Test
    void getTutorsBySubjectName_Coverage() {
        String subjectName = "математика";
        Pageable pageable = PageRequest.of(0, 10);

        Page<TutorEntity> entityPage = new PageImpl<>(List.of(tutorEntity));

        when(tutorRepository.findTutorsBySubjectName(subjectName, pageable)).thenReturn(entityPage);
        when(tutorMapper.toDomain(any(TutorEntity.class))).thenReturn(domainTutor);
        when(tutorMapper.toResponseDto(any(Tutor.class))).thenReturn(responseDto);
        when(queryCache.get(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> {
                    Supplier<Page<TutorResponseDto>> supplier = invocation.getArgument(6);
                    return supplier.get();
                });

        Page<TutorResponseDto> result = tutorService.getTutorsBySubjectName(subjectName, pageable);

        assertNotNull(result);
        verify(tutorRepository).findTutorsBySubjectName(subjectName, pageable);
    }

    @Test
    void getTutorsBySubjectNameNative_Coverage() {
        String subjectName = "математика";
        Pageable pageable = PageRequest.of(0, 10);

        Object[] row = new Object[]{1L, "Иван", "Петров", "ivan@mail.com", BigDecimal.valueOf(1500), 2018, 1L, "Математика", "Точные науки"};
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);
        Page<Object[]> entityPage = new PageImpl<>(rows, pageable, rows.size());

        when(tutorRepository.findTutorsBySubjectNameNative(subjectName, pageable)).thenReturn(entityPage);
        when(tutorMapper.toResponseDtoFromNative(any(Object[].class))).thenReturn(responseDto);
        when(queryCache.get(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> {
                    Supplier<Page<TutorResponseDto>> supplier = invocation.getArgument(6);
                    return supplier.get();
                });

        Page<TutorResponseDto> result = tutorService.getTutorsBySubjectNameNative(subjectName, pageable);

        assertNotNull(result);
        verify(tutorRepository).findTutorsBySubjectNameNative(subjectName, pageable);
    }

    @Test
    void getCachedTutors_SortByAscending() {
        String subjectName = "математика";
        Sort sort = Sort.by("hourlyRate").ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);

        when(queryCache.get(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> {
                    Supplier<Page<TutorResponseDto>> supplier = invocation.getArgument(6);
                    return supplier.get();
                });
        when(tutorRepository.findTutorsBySubjectName(subjectName, pageable)).thenReturn(new PageImpl<>(List.of()));

        tutorService.getTutorsBySubjectName(subjectName, pageable);

        verify(tutorRepository).findTutorsBySubjectName(subjectName, pageable);
    }

    @Test
    void getCachedTutors_SortByDescending() {
        String subjectName = "математика";
        Sort sort = Sort.by("hourlyRate").descending();
        Pageable pageable = PageRequest.of(0, 10, sort);

        when(queryCache.get(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> {
                    Supplier<Page<TutorResponseDto>> supplier = invocation.getArgument(6);
                    return supplier.get();
                });
        when(tutorRepository.findTutorsBySubjectName(subjectName, pageable)).thenReturn(new PageImpl<>(List.of()));

        tutorService.getTutorsBySubjectName(subjectName, pageable);

        verify(tutorRepository).findTutorsBySubjectName(subjectName, pageable);
    }

    @Test
    void getCachedTutors_NoSort() {
        String subjectName = "математика";
        Pageable pageable = PageRequest.of(0, 10);

        when(queryCache.get(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> {
                    Supplier<Page<TutorResponseDto>> supplier = invocation.getArgument(6);
                    return supplier.get();
                });
        when(tutorRepository.findTutorsBySubjectName(subjectName, pageable)).thenReturn(new PageImpl<>(List.of()));

        tutorService.getTutorsBySubjectName(subjectName, pageable);

        verify(tutorRepository).findTutorsBySubjectName(subjectName, pageable);
    }
}