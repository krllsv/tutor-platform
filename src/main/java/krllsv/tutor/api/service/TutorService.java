package krllsv.tutor.api.service;

import jakarta.persistence.EntityNotFoundException;
import krllsv.tutor.api.domain.Tutor;
import krllsv.tutor.api.entity.SubjectEntity;
import krllsv.tutor.api.repository.SubjectRepository;
import org.springframework.transaction.annotation.Transactional;
import krllsv.tutor.api.dto.request.TutorRequestDto;
import krllsv.tutor.api.entity.TutorEntity;
import krllsv.tutor.api.dto.response.TutorResponseDto;
import krllsv.tutor.api.mapper.TutorMapper;
import krllsv.tutor.api.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorService {
    private static final String NOT_FOUND = " not found.";

    private final TutorMapper tutorMapper;
    private final TutorRepository tutorRepository;
    private final SubjectRepository subjectRepository;

    public TutorService(TutorMapper tutorMapper,
                        TutorRepository tutorRepository,
                        SubjectRepository subjectRepository
    ) {
        this.tutorMapper = tutorMapper;
        this.tutorRepository = tutorRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public List<TutorResponseDto> getTutorsBySubjectName(String subjectName) {
        List<TutorEntity> tutors = tutorRepository.findTutorsBySubjectName(subjectName);

        return tutors.stream()
                .map(tutorMapper::toDomain)
                .map(tutorMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TutorResponseDto> getTutorsBySubjectNameNative(String subjectName) {
        List<TutorEntity> tutors = tutorRepository.findTutorsBySubjectNameNative(subjectName);

        return tutors.stream()
                .map(tutorMapper::toDomain)
                .map(tutorMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public TutorResponseDto createTutor(TutorRequestDto requestDto) {
        TutorEntity tutorEntity = tutorMapper.toEntity(requestDto);

        if (requestDto.getSubjectId() != null) {
            SubjectEntity subject = subjectRepository.findById(requestDto.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
            tutorEntity.setSubject(subject);
        }
        TutorEntity savedTutorEntity = tutorRepository.save(tutorEntity);
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
        return tutorMapper.toResponseDto(tutorMapper.toDomain(updatedEntity));
    }

    @Transactional
    public void deleteTutor(Long id) {
        if (!tutorRepository.existsById(id)) {
            throw new EntityNotFoundException("Tutor with id " + id + NOT_FOUND);
        }
        tutorRepository.deleteById(id);
    }
}