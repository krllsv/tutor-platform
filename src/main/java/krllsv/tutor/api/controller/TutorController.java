package krllsv.tutor.api.controller;

import krllsv.tutor.api.domain.Tutor;
import krllsv.tutor.api.service.TutorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tutor") // Все обработчики в классе будут начинаться со /tutor
public class TutorController {
    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping("/{id}")                  // Указываем, что id приходит в пути
    public ResponseEntity<Tutor> getTutorById(
            @PathVariable("id") Long id     // и этот id передается сюда в этот параметр
    ){
        System.out.println("Called getTutorById");
        return ResponseEntity.status(HttpStatus.OK)
                .body(tutorService.getTutorById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Tutor>> getAllTutors(){
        System.out.println("Called getAllTutors");
        return ResponseEntity.ok(tutorService.findAllTutors());
    }

    @PostMapping
    public ResponseEntity<Tutor> createTutor(
            @RequestBody Tutor tutorToCreate // tutor нужно взять из тела запроса
    ){
        System.out.println("Called createTutor");
        return ResponseEntity.status(HttpStatus.CREATED) // equal to 201 - created
                .body(tutorService.createTutor(tutorToCreate));
    }
}
