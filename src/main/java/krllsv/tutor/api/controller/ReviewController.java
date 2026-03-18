package krllsv.tutor.api.controller;

import krllsv.tutor.api.dto.request.ReviewRequestDto;
import krllsv.tutor.api.dto.response.ReviewResponseDto;
import krllsv.tutor.api.service.ReviewService;
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
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto created = reviewService.createReview(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getAllReviews() {
        List<ReviewResponseDto> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long id) {
        ReviewResponseDto review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto updated = reviewService.updateReview(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-tutor/{tutorId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByTutor(@PathVariable Long tutorId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByTutor(tutorId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByStudent(@PathVariable Long studentId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByStudent(studentId);
        return ResponseEntity.ok(reviews);
    }
}