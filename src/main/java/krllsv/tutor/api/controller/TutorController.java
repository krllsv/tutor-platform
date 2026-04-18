package krllsv.tutor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import krllsv.tutor.api.dto.request.TutorBulkRequestDto;
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

    @PostMapping("/bulk")
    @Operation(summary = "Массовое создание преподавателей",
            description = "Создает несколько преподавателей за один запрос")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Преподаватели созданы"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<List<TutorResponseDto>> createTutorsBulk(
            @Valid @RequestBody TutorBulkRequestDto requestDto) {

        List<TutorResponseDto> created = tutorService.createTutorsBulk(requestDto.getTutors());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/bulk/without-tx")
    @Operation(summary = "Массовое создание БЕЗ транзакции",
            description = "Демонстрация частичного сохранения при ошибке")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Частично создано"),
            @ApiResponse(responseCode = "500", description = "Ошибка, но часть данных сохранена")
    })
    public ResponseEntity<List<TutorResponseDto>> createTutorsBulkWithoutTransaction(
            @Valid @RequestBody TutorBulkRequestDto requestDto
    ) {
        List<TutorResponseDto> result = tutorService.createTutorsBulkWithoutTransaction(requestDto.getTutors());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/demo/stream/sorted-by-rate")
    @Operation(summary = "Получить преподавателей, отсортированных по ставке",
            description = "Демонстрация Stream API: sorted + Comparator")
    @ApiResponse(responseCode = "200", description = "Список преподавателей успешно получен")
    public ResponseEntity<List<TutorResponseDto>> getTutorsSortedByHourlyRate() {
        return ResponseEntity.ok(tutorService.getTutorsSortedByHourlyRate());
    }

    @GetMapping("/demo/stream/by-rating")
    @Operation(summary = "Получить преподавателей с рейтингом выше указанного",
            description = "Демонстрация Stream API: filter с вычислением среднего рейтинга из отзывов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список преподавателей успешно получен"),
            @ApiResponse(responseCode = "400", description = "Некорректный параметр")
    })
    public ResponseEntity<List<TutorResponseDto>> getTutorsByMinRating(
            @Parameter(description = "Минимальный рейтинг", example = "4.0")
            @RequestParam(defaultValue = "4.0") double minRating) {
        return ResponseEntity.ok(tutorService.getTutorsByMinRating(minRating));
    }

}