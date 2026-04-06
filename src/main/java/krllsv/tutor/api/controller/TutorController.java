package krllsv.tutor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/tutors") // Все обработчики в классе будут начинаться со /tutor4
@Tag(name = "Tutor Controller", description = "Управление репетиторами")
public class TutorController {
    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping("/by-subject")
    @Operation(summary = "Поиск преподавателей по предмету (JPQL)",
            description = "Возвращает преподавателей, ведущих указанный предмет. Поддерживает пагинацию и сортировку.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно"),
                            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
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
    @Operation(summary = "Поиск преподавателей по предмету (Native Query)",
            description = "Возвращает преподавателей, ведущих указанный предмет. Поддерживает пагинацию и сортировку.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно"),
                            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    public ResponseEntity<Page<TutorResponseDto>> getTutorsBySubjectNameNative(
            @RequestParam String subject,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TutorResponseDto> result = tutorService.getTutorsBySubjectNameNative(subject, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Создать преподавателя", description = "Создаёт нового преподавателя")
    @ApiResponse(responseCode = "201", description = "Преподаватель создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    public ResponseEntity<TutorResponseDto> createTutor(@Valid @RequestBody TutorRequestDto tutorRequestDto) {
        TutorResponseDto created = tutorService.createTutor(tutorRequestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Получить всех преподавателей", description = "Возвращает список всех преподавателей")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public ResponseEntity<List<TutorResponseDto>> getAllTutors() {
        List<TutorResponseDto> tutors = tutorService.getAllTutors();
        return ResponseEntity.ok(tutors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить преподавателя по ID", description = "Возвращает преподавателя по указанному ID")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Преподаватель не найден")
    public ResponseEntity<TutorResponseDto> getTutorById(@PathVariable Long id) {
        TutorResponseDto tutor = tutorService.getTutorById(id);
        return ResponseEntity.ok(tutor);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить преподавателя", description = "Обновляет данные преподавателя по ID")
    @ApiResponse(responseCode = "200", description = "Успешно обновлено")
    @ApiResponse(responseCode = "404", description = "Преподаватель не найден")
    public ResponseEntity<TutorResponseDto> updateTutor(
            @PathVariable Long id,
            @Valid @RequestBody TutorRequestDto requestDto) {
        TutorResponseDto updated = tutorService.updateTutor(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить преподавателя", description = "Удаляет преподавателя по ID")
    @ApiResponse(responseCode = "204", description = "Успешно удалено")
    @ApiResponse(responseCode = "404", description = "Преподаватель не найден")
    public ResponseEntity<Void> deleteTutor(@PathVariable Long id) {
        tutorService.deleteTutor(id);
        return ResponseEntity.noContent().build();
    }
}