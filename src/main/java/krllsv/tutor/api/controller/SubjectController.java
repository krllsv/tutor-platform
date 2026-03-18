package krllsv.tutor.api.controller;

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
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<SubjectResponseDto> createSubject(@RequestBody SubjectRequestDto requestDto) {
        SubjectResponseDto created = subjectService.createSubject(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponseDto>> getAllSubjects() {
        List<SubjectResponseDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> getSubjectById(@PathVariable Long id) {
        SubjectResponseDto subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> updateSubject(
            @PathVariable Long id,
            @RequestBody SubjectRequestDto requestDto) {
        SubjectResponseDto updated = subjectService.updateSubject(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
