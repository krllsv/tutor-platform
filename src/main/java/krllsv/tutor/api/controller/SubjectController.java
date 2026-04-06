package krllsv.tutor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import krllsv.tutor.api.dto.request.SubjectRequestDto;
import krllsv.tutor.api.dto.response.SubjectResponseDto;
import krllsv.tutor.api.service.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@Tag(name = "Subject Controller", description = "Управление предметами")
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    @Operation(summary = "Создать предмет", description = "Создаёт новый учебный предмет")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Предмет создан"),
                            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
                            @ApiResponse(responseCode = "409", description = "Предмет с таким названием уже существует")
    })
    public ResponseEntity<SubjectResponseDto> createSubject(@RequestBody SubjectRequestDto requestDto) {
        SubjectResponseDto created = subjectService.createSubject(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Получить все предметы", description = "Возвращает список всех учебных предметов")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public ResponseEntity<List<SubjectResponseDto>> getAllSubjects() {
        List<SubjectResponseDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить предмет по ID", description = "Возвращает предмет по указанному идентификатору")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно"),
                            @ApiResponse(responseCode = "404", description = "Предмет не найден")
    })
    public ResponseEntity<SubjectResponseDto> getSubjectById(
            @Parameter(description = "ID предмета", example = "1") @PathVariable Long id
    ) {
        SubjectResponseDto subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить предмет", description = "Обновляет данные предмета по ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно обновлено"),
                            @ApiResponse(responseCode = "404", description = "Предмет не найден"),
                            @ApiResponse(responseCode = "409", description = "Предмет с таким названием уже существует")
    })
    public ResponseEntity<SubjectResponseDto> updateSubject(
            @Parameter(description = "ID предмета", example = "1") @PathVariable Long id,
            @RequestBody SubjectRequestDto requestDto) {
        SubjectResponseDto updated = subjectService.updateSubject(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить предмет", description = "Удаляет предмет по ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Успешно удалено"),
                            @ApiResponse(responseCode = "404", description = "Предмет не найден")
    })
    public ResponseEntity<Void> deleteSubject(
            @Parameter(description = "ID предмета", example = "1") @PathVariable Long id
    ) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
