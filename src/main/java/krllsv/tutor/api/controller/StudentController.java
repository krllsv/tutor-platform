package krllsv.tutor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import krllsv.tutor.api.dto.request.StudentRequestDto;
import krllsv.tutor.api.dto.response.StudentResponseDto;
import krllsv.tutor.api.service.StudentService;
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
@RequestMapping("/api/students")
@Tag(name = "Student Controller", description = "Управление студентами")
public class StudentController {
    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/demo/without-tx")
    @Operation(summary = "Демонстрация без транзакции", description = "Показывает частичное сохранение при ошибке")
    @ApiResponse(responseCode = "200", description = "Метод выполнен")
    public void demoWithoutTx(@RequestBody StudentRequestDto requestDto) {
        studentService.createWithoutTransaction(requestDto);
    }

    @PostMapping("/demo/with-tx")
    @Operation(summary = "Демонстрация с транзакцией", description = "Показывает полный откат при ошибке")
    @ApiResponse(responseCode = "200", description = "Метод выполнен")
    public void demoWithTx(@RequestBody StudentRequestDto requestDto) {
        studentService.createWithTransaction(requestDto);
    }

    @GetMapping("/demo-nplus1/problem")
    @Operation(summary = "Демонстрация проблемы N+1", description = "Показывает проблему N+1 в консоли")
    @ApiResponse(responseCode = "200", description = "Метод выполнен")
    public void demoProblem() {
        studentService.getAllStudentsWithProblem();
    }

    @GetMapping("/demo-nplus1/solution")
    @Operation(summary = "Демонстрация решения N+1", description = "Показывает решение проблемы N+1")
    @ApiResponse(responseCode = "200", description = "Метод выполнен")
    public void demoSolution() {
        studentService.getAllStudents();
    }

    @PostMapping
    @Operation(summary = "Создать студента", description = "Создаёт нового студента")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Студент создан"),
                            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<StudentResponseDto> createStudent(@RequestBody StudentRequestDto requestDto) {
        StudentResponseDto created = studentService.createStudent(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Получить всех студентов", description = "Возвращает список всех студентов с их предметами")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        List<StudentResponseDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить студента по ID", description = "Возвращает студента по указанному идентификатору")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно"),
                            @ApiResponse(responseCode = "404", description = "Студент не найден")
    })
    public ResponseEntity<StudentResponseDto> getStudentById(
            @Parameter(description = "ID студента", example = "1") @PathVariable Long id
    ) {
        StudentResponseDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить студента", description = "Обновляет данные студента по ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно обновлено"),
                            @ApiResponse(responseCode = "404", description = "Студент не найден")
    })
    public ResponseEntity<StudentResponseDto> updateStudent(
            @Parameter(description = "ID студента", example = "1") @PathVariable Long id,
            @RequestBody StudentRequestDto requestDto) {
        StudentResponseDto updated = studentService.updateStudent(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить студента", description = "Удаляет студента по ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Успешно удалено"),
                            @ApiResponse(responseCode = "404", description = "Студент не найден")
    })
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "ID студента", example = "1") @PathVariable Long id
    ) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

}
