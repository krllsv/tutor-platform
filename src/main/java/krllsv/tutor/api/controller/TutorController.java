package krllsv.tutor.api.controller;

import krllsv.tutor.api.dto.request.TutorRequestDto;
import krllsv.tutor.api.dto.response.TutorResponseDto;
import krllsv.tutor.api.service.TutorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tutors") // Все обработчики в классе будут начинаться со /tutor
public class TutorController {
    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping("/by-subject")
    public ResponseEntity<Page<TutorResponseDto>> getTutorsBySubjectName(
            @RequestParam String subject,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TutorResponseDto> tutors = tutorService.getTutorsBySubjectName(subject, pageable);
        return ResponseEntity.ok(tutors);
    }

    @GetMapping("/by-subject-native")
    public ResponseEntity<Page<TutorResponseDto>> getTutorsBySubjectNameNative(
            @RequestParam String subject,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TutorResponseDto> tutors = tutorService.getTutorsBySubjectNameNative(subject, pageable);
        return ResponseEntity.ok(tutors);
    }

    @PostMapping
    public ResponseEntity<TutorResponseDto> createTutor(@RequestBody TutorRequestDto tutorRequestDto) {
        TutorResponseDto created = tutorService.createTutor(tutorRequestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TutorResponseDto>> getAllTutors() {
        List<TutorResponseDto> tutors = tutorService.getAllTutors();
        return ResponseEntity.ok(tutors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorResponseDto> getTutorById(@PathVariable Long id) {
        TutorResponseDto tutor = tutorService.getTutorById(id);
        return ResponseEntity.ok(tutor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TutorResponseDto> updateTutor(
            @PathVariable Long id,
            @RequestBody TutorRequestDto requestDto) {
        TutorResponseDto updated = tutorService.updateTutor(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTutor(@PathVariable Long id) {
        tutorService.deleteTutor(id);
        return ResponseEntity.noContent().build();
    }
}