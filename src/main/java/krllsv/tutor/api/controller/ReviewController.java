package krllsv.tutor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Review Controller", description = "Управление отзывами")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Создать отзыв", description = "Создаёт новый отзыв о преподавателе")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Отзыв создан"),
                            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
                            @ApiResponse(responseCode = "404", description = "Студент или преподаватель не найден")
    })
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto created = reviewService.createReview(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Получить все отзывы", description = "Возвращает список всех отзывов")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public ResponseEntity<List<ReviewResponseDto>> getAllReviews() {
        List<ReviewResponseDto> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить отзыв по ID", description = "Возвращает отзыв по указанному идентификатору")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно"),
                            @ApiResponse(responseCode = "404", description = "Отзыв не найден")
    })
    public ResponseEntity<ReviewResponseDto> getReviewById(
            @Parameter(description = "ID отзыва", example = "1") @PathVariable Long id
    ) {
        ReviewResponseDto review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить отзыв", description = "Обновляет отзыв по ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно обновлено"),
                            @ApiResponse(responseCode = "404", description = "Отзыв не найден")
    })
    public ResponseEntity<ReviewResponseDto> updateReview(
            @Parameter(description = "ID отзыва", example = "1") @PathVariable Long id,
            @RequestBody ReviewRequestDto requestDto
    ) {
        ReviewResponseDto updated = reviewService.updateReview(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить отзыв", description = "Удаляет отзыв по ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Успешно удалено"),
                            @ApiResponse(responseCode = "404", description = "Отзыв не найден")
    })
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID отзыва", example = "1") @PathVariable Long id
    ) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-tutor/{tutorId}")
    @Operation(summary = "Получить отзывы преподавателя",
            description = "Возвращает все отзывы о указанном преподавателе")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByTutor(
            @Parameter(description = "ID преподавателя", example = "1") @PathVariable Long tutorId
    ) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByTutor(tutorId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/by-student/{studentId}")
    @Operation(summary = "Получить отзывы студента",
            description = "Возвращает все отзывы, оставленные указанным студентом")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByStudent(
            @Parameter(description = "ID студента", example = "1") @PathVariable Long studentId
    ) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByStudent(studentId);
        return ResponseEntity.ok(reviews);
    }
}