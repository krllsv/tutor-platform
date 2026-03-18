package krllsv.tutor.api.controller;

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
public class StudentController {
    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/demo/without-tx")
    public void demoWithoutTx(@RequestBody StudentRequestDto requestDto) {
        studentService.createWithoutTransaction(requestDto);
    }

    @PostMapping("/demo/with-tx")
    public void demoWithTx(@RequestBody StudentRequestDto requestDto) {
        studentService.createWithTransaction(requestDto);
    }

    @GetMapping("/demo-nplus1/problem")
    public void demoProblem() {
        studentService.getAllStudentsWithProblem();
    }

    @GetMapping("/demo-nplus1/solution")
    public void demoSolution() {
        studentService.getAllStudents();
    }

    @PostMapping
    public ResponseEntity<StudentResponseDto> createStudent(@RequestBody StudentRequestDto requestDto) {
        StudentResponseDto created = studentService.createStudent(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        List<StudentResponseDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        StudentResponseDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequestDto requestDto) {
        StudentResponseDto updated = studentService.updateStudent(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

}
