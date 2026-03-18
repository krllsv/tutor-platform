package krllsv.tutor.api.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import krllsv.tutor.api.domain.Subject;
import krllsv.tutor.api.dto.request.SubjectRequestDto;
import krllsv.tutor.api.dto.response.SubjectResponseDto;
import krllsv.tutor.api.entity.SubjectEntity;
import krllsv.tutor.api.mapper.SubjectMapper;
import krllsv.tutor.api.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public SubjectService(SubjectRepository subjectRepository, SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
    }

    @Transactional
    public SubjectResponseDto createSubject(SubjectRequestDto requestDto) {
        SubjectEntity entity = subjectMapper.toEntity(requestDto);
        SubjectEntity savedEntity = subjectRepository.save(entity);
        Subject subject = subjectMapper.toDomain(savedEntity);
        return subjectMapper.toResponseDto(subject);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toDomain)
                .map(subjectMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponseDto getSubjectById(Long id) {
        SubjectEntity entity = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));

        Subject subject = subjectMapper.toDomain(entity);
        return subjectMapper.toResponseDto(subject);
    }

    @Transactional
    public SubjectResponseDto updateSubject(Long id, SubjectRequestDto requestDto) {
        SubjectEntity existingEntity = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));

        existingEntity.setName(requestDto.getName());
        existingEntity.setCategory(requestDto.getCategory());
        existingEntity.setDescription(requestDto.getDescription());

        SubjectEntity updatedEntity = subjectRepository.save(existingEntity);

        Subject subject = subjectMapper.toDomain(updatedEntity);
        return subjectMapper.toResponseDto(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }
}
