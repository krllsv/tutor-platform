package krllsv.tutor.api.service;

import krllsv.tutor.api.domain.Tutor;
import krllsv.tutor.api.dto.TutorResponseDto;
import krllsv.tutor.api.mapper.TutorMapper;
import krllsv.tutor.api.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final TutorMapper tutorMapper;

    public TutorService(TutorRepository tutorRepository, TutorMapper tutorMapper) {
        this.tutorRepository = tutorRepository;
        this.tutorMapper = tutorMapper;
    }

    public List<TutorResponseDto> findAllTutors() {
        return tutorRepository.findAll().stream()
                .map(tutorMapper::toDto)
                .toList();
    }

    public TutorResponseDto getTutorById(Long id) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Репетитор не найден"));

        return tutorMapper.toDto(tutor);
    }

    public List<TutorResponseDto> getTutorBySubject(String subject) {
        return tutorRepository.findBySubject(subject).stream()
                .map(tutorMapper::toDto)
                .toList();
    }
}
