package krllsv.tutor.api.controller;

import krllsv.tutor.api.dto.TutorResponseDto;
import krllsv.tutor.api.mapper.TutorMapper;
import krllsv.tutor.api.service.TutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/tutor") // Все обработчики в классе будут начинаться со /tutor
public class TutorController {
    private final TutorService tutorService;
    private final TutorMapper tutorMapper;

    public TutorController(TutorService tutorService, TutorMapper tutorMapper) {
        this.tutorService = tutorService;
        this.tutorMapper = tutorMapper;
    }

    @GetMapping("/{id}")                  // Указываем, что id приходит в пути
    public ResponseEntity<TutorResponseDto> getTutorById(
            @PathVariable("id") Long id     // и этот id передается сюда в этот параметр
    ) {
        TutorResponseDto tutor = tutorService.getTutorById(id);
        return ResponseEntity.ok(tutor);
    }

    @GetMapping
    public ResponseEntity<List<TutorResponseDto>> getTutorsBySubject(
            @RequestParam(value = "subject", required = false) String subject
    ) {
        List<TutorResponseDto> tutors;
        if (subject != null && !subject.isEmpty()) {
            tutors = tutorService.getTutorBySubject(subject);
        } else {
            tutors = tutorService.findAllTutors();
        }
        return ResponseEntity.ok(tutors);
    }
}
