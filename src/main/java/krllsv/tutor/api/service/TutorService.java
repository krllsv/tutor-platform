package krllsv.tutor.api.service;

import jakarta.persistence.EntityNotFoundException;
import krllsv.tutor.api.cache.QueryCache;
import krllsv.tutor.api.domain.Tutor;
import krllsv.tutor.api.entity.ReviewEntity;
import krllsv.tutor.api.entity.SubjectEntity;
import krllsv.tutor.api.repository.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import krllsv.tutor.api.dto.request.TutorRequestDto;
import krllsv.tutor.api.entity.TutorEntity;
import krllsv.tutor.api.dto.response.TutorResponseDto;
import krllsv.tutor.api.mapper.TutorMapper;
import krllsv.tutor.api.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class TutorService {
    private static final String NOT_FOUND = " not found.";
    private static final String ENDPOINT_BY_SUBJECT = "by-subject";
    private static final String ENDPOINT_BY_SUBJECT_NATIVE = "by-subject-native";

    private final TutorMapper tutorMapper;
    private final TutorRepository tutorRepository;
    private final SubjectRepository subjectRepository;
    private final QueryCache queryCache;

    public TutorService(TutorMapper tutorMapper,
                        TutorRepository tutorRepository,
                        SubjectRepository subjectRepository,
                        QueryCache queryCache
    ) {
        this.tutorMapper = tutorMapper;
        this.tutorRepository = tutorRepository;
        this.subjectRepository = subjectRepository;
        this.queryCache = queryCache;
    }

    @Transactional(readOnly = true)
    public Page<TutorResponseDto> getTutorsBySubjectName(String subjectName, Pageable pageable) {
        log.info("JPQL query: searching tutors by subject: {}", subjectName);
        return getCachedTutors(ENDPOINT_BY_SUBJECT, subjectName, pageable, false);
    }

    @Transactional(readOnly = true)
    public Page<TutorResponseDto> getTutorsBySubjectNameNative(String subjectName, Pageable pageable) {
        log.info("Native query: searching tutors by subject: {}", subjectName);
        return getCachedTutors(ENDPOINT_BY_SUBJECT_NATIVE, subjectName, pageable, true);
    }

    private Page<TutorResponseDto> getCachedTutors(
            String endpoint,
            String subjectName,
            Pageable pageable,
            boolean isNative
    ) {
        String sortBy = pageable.getSort().isSorted() ?
                pageable.getSort().iterator().next().getProperty() : "id";
        String sortDir = pageable.getSort().isSorted() &&
                pageable.getSort().iterator().next().isAscending() ? "asc" : "desc";

        return queryCache.get(endpoint, subjectName,
                pageable.getPageNumber(), pageable.getPageSize(),
                sortBy, sortDir,
                () -> {
                    if (isNative) {
                        Page<Object[]> page = tutorRepository.findTutorsBySubjectNameNative(subjectName, pageable);
                        List<TutorResponseDto> content = page.getContent().stream()
                                .map(tutorMapper::toResponseDtoFromNative)
                                .toList();
                        return new PageImpl<>(content, pageable, page.getTotalElements());
                    } else {
                        Page<TutorEntity> tutors = tutorRepository.findTutorsBySubjectName(subjectName, pageable);
                        return tutors.map(tutorMapper::toDomain)
                                .map(tutorMapper::toResponseDto);
                    }
                });
    }

    @Transactional
    public TutorResponseDto createTutor(TutorRequestDto requestDto) {
        TutorEntity tutorEntity = tutorMapper.toEntity(requestDto);

        if (tutorRepository.existsByEmail(requestDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tutor with email " +
                    requestDto.getEmail() + " already exists");
        }

        if (requestDto.getSubjectId() != null) {
            SubjectEntity subject = subjectRepository.findById(requestDto.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
            tutorEntity.setSubject(subject);
        }
        TutorEntity savedTutorEntity = tutorRepository.save(tutorEntity);

        queryCache.invalidateByEndpoint(ENDPOINT_BY_SUBJECT);
        queryCache.invalidateByEndpoint(ENDPOINT_BY_SUBJECT_NATIVE);

        Tutor tutor = tutorMapper.toDomain(savedTutorEntity);
        return tutorMapper.toResponseDto(tutor);
    }

    @Transactional(readOnly = true)
    public List<TutorResponseDto> getAllTutors() {
        List<TutorEntity> tutors = tutorRepository.findAllWithSubject();
        return tutors.stream()
                .map(tutorMapper::toDomain)
                .map(tutorMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TutorResponseDto getTutorById(Long id) {
        TutorEntity tutorEntity = tutorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tutor with id " + id + NOT_FOUND));

        return tutorMapper.toResponseDto(tutorMapper.toDomain(tutorEntity));
    }

    @Transactional
    public TutorResponseDto updateTutor(Long id, TutorRequestDto requestDto) {
        TutorEntity existingEntity = tutorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tutor with id " + id + NOT_FOUND));

        existingEntity.setFirstName(requestDto.getFirstName());
        existingEntity.setLastName(requestDto.getLastName());
        existingEntity.setHourlyRate(requestDto.getHourlyRate());
        existingEntity.setStartYear(requestDto.getStartYear());
        existingEntity.setEmail(requestDto.getEmail());

        if (requestDto.getSubjectId() != null) {
            SubjectEntity subject = subjectRepository.findById(requestDto.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Subject with id " + id + NOT_FOUND));
            existingEntity.setSubject(subject);
        } else {
            existingEntity.setSubject(null);
        }
        TutorEntity updatedEntity = tutorRepository.save(existingEntity);

        queryCache.invalidateByEndpoint(ENDPOINT_BY_SUBJECT);
        queryCache.invalidateByEndpoint(ENDPOINT_BY_SUBJECT_NATIVE);

        return tutorMapper.toResponseDto(tutorMapper.toDomain(updatedEntity));
    }

    @Transactional
    public void deleteTutor(Long id) {
        if (!tutorRepository.existsById(id)) {
            throw new EntityNotFoundException("Tutor with id " + id + NOT_FOUND);
        }
        tutorRepository.deleteById(id);

        queryCache.invalidateByEndpoint(ENDPOINT_BY_SUBJECT);
        queryCache.invalidateByEndpoint(ENDPOINT_BY_SUBJECT_NATIVE);
    }

    public List<TutorResponseDto> createTutorsBulkWithoutTransaction(List<TutorRequestDto> requestDtos) {
        log.info("Bulk create WITHOUT @Transactional: {} tutors", requestDtos.size());

        return requestDtos.stream()
                .map(this::createTutor)
                .toList();
    }

    @Transactional
    public List<TutorResponseDto> createTutorsBulk(List<TutorRequestDto> requestDtos) {
        log.info("Bulk create with @Transactional: {} tutors", requestDtos.size());

        return requestDtos.stream()
                .map(this::createTutor)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TutorResponseDto> getTutorsSortedByHourlyRate() {
        log.info("Stream API: sorting tutors by hourly rate (cheapest first)");

        return tutorRepository.findAll().stream()
                .sorted(Comparator.comparing(TutorEntity::getHourlyRate))
                .map(tutorMapper::toDomain)
                .map(tutorMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TutorResponseDto> getTutorsByMinRating(double minRating) {
        log.info("Stream API: filtering tutors with average rating >= {}", minRating);

        return tutorRepository.findAll().stream()
                .filter(t -> {
                    double avgRating = t.getReviews().stream()
                            .mapToInt(ReviewEntity::getRating)
                            .average()
                            .orElse(0.0);
                    return avgRating >= minRating;
                })
                .map(tutorMapper::toDomain)
                .map(tutorMapper::toResponseDto)
                .toList();
    }
}