package krllsv.tutor.api.service;

import jakarta.persistence.EntityNotFoundException;
import krllsv.tutor.api.domain.Review;
import krllsv.tutor.api.dto.request.ReviewRequestDto;
import krllsv.tutor.api.dto.response.ReviewResponseDto;
import krllsv.tutor.api.entity.ReviewEntity;
import krllsv.tutor.api.entity.StudentEntity;
import krllsv.tutor.api.entity.TutorEntity;
import krllsv.tutor.api.mapper.ReviewMapper;
import krllsv.tutor.api.repository.ReviewRepository;
import krllsv.tutor.api.repository.StudentRepository;
import krllsv.tutor.api.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewRequestDto requestDto;
    private ReviewEntity reviewEntity;
    private ReviewEntity savedReviewEntity;
    private StudentEntity studentEntity;
    private TutorEntity tutorEntity;
    private Review domainReview;
    private ReviewResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new ReviewRequestDto();
        requestDto.setRating(5);
        requestDto.setComment("Отличный преподаватель!");
        requestDto.setStudentId(1L);
        requestDto.setTutorId(1L);

        studentEntity = new StudentEntity();
        studentEntity.setId(1L);
        studentEntity.setFirstName("Иван");

        tutorEntity = new TutorEntity();
        tutorEntity.setId(1L);
        tutorEntity.setFirstName("Петр");

        reviewEntity = new ReviewEntity();
        reviewEntity.setRating(5);
        reviewEntity.setComment("Отличный преподаватель!");

        savedReviewEntity = new ReviewEntity();
        savedReviewEntity.setId(1L);
        savedReviewEntity.setRating(5);
        savedReviewEntity.setComment("Отличный преподаватель!");
        savedReviewEntity.setStudent(studentEntity);
        savedReviewEntity.setTutor(tutorEntity);

        domainReview = new Review();
        domainReview.setId(1L);
        domainReview.setRating(5);
        domainReview.setComment("Отличный преподаватель!");

        responseDto = new ReviewResponseDto();
        responseDto.setId(1L);
        responseDto.setRating(5);
        responseDto.setComment("Отличный преподаватель!");
    }

    @Test
    void createReview_ShouldSaveReview_WhenValidData() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorEntity));
        when(reviewMapper.toEntity(requestDto)).thenReturn(reviewEntity);
        when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(savedReviewEntity);
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        ReviewResponseDto result = reviewService.createReview(requestDto);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(reviewRepository).save(any(ReviewEntity.class));
    }

    @Test
    void createReview_ShouldThrowException_WhenStudentNotFound() {
        Long invalidStudentId = 999L;

        when(studentRepository.findById(invalidStudentId)).thenReturn(Optional.empty());

        ReviewRequestDto invalidRequest = new ReviewRequestDto();
        invalidRequest.setRating(5);
        invalidRequest.setComment("Отзыв");
        invalidRequest.setStudentId(invalidStudentId);
        invalidRequest.setTutorId(1L);

        assertThrows(EntityNotFoundException.class, () -> reviewService.createReview(invalidRequest));
        verify(studentRepository).findById(invalidStudentId);
        verify(tutorRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(ReviewEntity.class));
    }

    @Test
    void createReview_ShouldThrowException_WhenTutorNotFound() {
        Long invalidTutorId = 999L;

        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));
        when(tutorRepository.findById(invalidTutorId)).thenReturn(Optional.empty());

        ReviewRequestDto invalidRequest = new ReviewRequestDto();
        invalidRequest.setRating(5);
        invalidRequest.setComment("Отзыв");
        invalidRequest.setStudentId(1L);
        invalidRequest.setTutorId(invalidTutorId);

        assertThrows(EntityNotFoundException.class, () -> reviewService.createReview(invalidRequest));
        verify(studentRepository).findById(1L);
        verify(tutorRepository).findById(invalidTutorId);
        verify(reviewRepository, never()).save(any(ReviewEntity.class));
    }

    @Test
    void getReviewById_ShouldReturnReview_WhenExists() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(savedReviewEntity));
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        ReviewResponseDto result = reviewService.getReviewById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getReviewById_ShouldThrowException_WhenNotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reviewService.getReviewById(999L));
    }

    @Test
    void getAllReviews_ShouldReturnList() {
        when(reviewRepository.findAllWithDetails()).thenReturn(List.of(savedReviewEntity));
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        List<ReviewResponseDto> result = reviewService.getAllReviews();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository).findAllWithDetails();
    }

    @Test
    void getReviewsByTutor_ShouldReturnList() {
        Long tutorId = 1L;
        when(reviewRepository.findByTutorId(tutorId)).thenReturn(List.of(savedReviewEntity));
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        List<ReviewResponseDto> result = reviewService.getReviewsByTutor(tutorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository).findByTutorId(tutorId);
    }

    @Test
    void getReviewsByStudent_ShouldReturnList() {
        Long studentId = 1L;
        when(reviewRepository.findByStudentId(studentId)).thenReturn(List.of(savedReviewEntity));
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        List<ReviewResponseDto> result = reviewService.getReviewsByStudent(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository).findByStudentId(studentId);
    }

    @Test
    void updateReview_ShouldUpdate_WhenExists() {
        Long id = 1L;
        ReviewRequestDto updateRequest = new ReviewRequestDto();
        updateRequest.setRating(4);
        updateRequest.setComment("Обновлённый отзыв");
        updateRequest.setStudentId(1L);
        updateRequest.setTutorId(1L);

        when(reviewRepository.findById(id)).thenReturn(Optional.of(savedReviewEntity));
        when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(savedReviewEntity);
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        ReviewResponseDto result = reviewService.updateReview(id, updateRequest);

        assertNotNull(result);
        verify(reviewRepository).save(any(ReviewEntity.class));
    }

    @Test
    void updateReview_ShouldThrowException_WhenNotFound() {
        Long id = 999L;
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reviewService.updateReview(id, requestDto));
    }

    @Test
    void updateReview_ShouldUpdateStudent_WhenStudentIdChanged() {
        Long id = 1L;
        Long newStudentId = 2L;

        ReviewRequestDto updateRequest = new ReviewRequestDto();
        updateRequest.setRating(4);
        updateRequest.setComment("Обновлённый отзыв");
        updateRequest.setStudentId(newStudentId);
        updateRequest.setTutorId(1L);

        StudentEntity newStudent = new StudentEntity();
        newStudent.setId(newStudentId);
        newStudent.setFirstName("Новый студент");

        when(reviewRepository.findById(id)).thenReturn(Optional.of(savedReviewEntity));
        when(studentRepository.findById(newStudentId)).thenReturn(Optional.of(newStudent));
        when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(savedReviewEntity);
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        ReviewResponseDto result = reviewService.updateReview(id, updateRequest);

        assertNotNull(result);
        verify(studentRepository).findById(newStudentId);
        verify(reviewRepository).save(any(ReviewEntity.class));
    }

    @Test
    void updateReview_ShouldUpdateTutor_WhenTutorIdChanged() {
        Long id = 1L;
        Long newTutorId = 2L;

        ReviewRequestDto updateRequest = new ReviewRequestDto();
        updateRequest.setRating(4);
        updateRequest.setComment("Обновлённый отзыв");
        updateRequest.setStudentId(1L);
        updateRequest.setTutorId(newTutorId);

        TutorEntity newTutor = new TutorEntity();
        newTutor.setId(newTutorId);
        newTutor.setFirstName("Новый преподаватель");

        when(reviewRepository.findById(id)).thenReturn(Optional.of(savedReviewEntity));
        when(tutorRepository.findById(newTutorId)).thenReturn(Optional.of(newTutor));
        when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(savedReviewEntity);
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        ReviewResponseDto result = reviewService.updateReview(id, updateRequest);

        assertNotNull(result);
        verify(tutorRepository).findById(newTutorId);
        verify(reviewRepository).save(any(ReviewEntity.class));
    }

    @Test
    void updateReview_ShouldNotUpdateStudentOrTutor_WhenIdsAreNull() {
        Long id = 1L;
        ReviewRequestDto updateRequest = new ReviewRequestDto();
        updateRequest.setRating(4);
        updateRequest.setComment("Обновлённый отзыв");
        updateRequest.setStudentId(null);
        updateRequest.setTutorId(null);

        when(reviewRepository.findById(id)).thenReturn(Optional.of(savedReviewEntity));
        when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(savedReviewEntity);
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        ReviewResponseDto result = reviewService.updateReview(id, updateRequest);

        assertNotNull(result);
        verify(studentRepository, never()).findById(anyLong());
        verify(tutorRepository, never()).findById(anyLong());
        verify(reviewRepository).save(any(ReviewEntity.class));
    }

    @Test
    void updateReview_ShouldNotUpdateStudentOrTutor_WhenIdsSame() {
        Long id = 1L;
        ReviewRequestDto updateRequest = new ReviewRequestDto();
        updateRequest.setRating(4);
        updateRequest.setComment("Обновлённый отзыв");
        updateRequest.setStudentId(1L);
        updateRequest.setTutorId(1L);

        when(reviewRepository.findById(id)).thenReturn(Optional.of(savedReviewEntity));
        when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(savedReviewEntity);
        when(reviewMapper.toDomain(any(ReviewEntity.class))).thenReturn(domainReview);
        when(reviewMapper.toResponseDto(any(Review.class))).thenReturn(responseDto);

        ReviewResponseDto result = reviewService.updateReview(id, updateRequest);

        assertNotNull(result);
        verify(studentRepository, never()).findById(anyLong());
        verify(tutorRepository, never()).findById(anyLong());
        verify(reviewRepository).save(any(ReviewEntity.class));
    }

    @Test
    void deleteReview_ShouldDelete_WhenExists() {
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.deleteReview(1L);

        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void deleteReview_ShouldThrowException_WhenNotFound() {
        when(reviewRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> reviewService.deleteReview(999L));
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}