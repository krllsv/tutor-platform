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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final TutorRepository tutorRepository;
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository,
                         StudentRepository studentRepository,
                         TutorRepository tutorRepository,
                         ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
        this.reviewMapper = reviewMapper;
    }

    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto requestDto) {
        StudentEntity student = studentRepository.findById(requestDto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " +
                        requestDto.getStudentId()));

        TutorEntity tutor = tutorRepository.findById(requestDto.getTutorId())
                .orElseThrow(() -> new EntityNotFoundException("Tutor not found with id: " +
                        requestDto.getTutorId()));

        ReviewEntity entity = reviewMapper.toEntity(requestDto);
        entity.setStudent(student);
        entity.setTutor(tutor);
        entity.setCreatedAt(LocalDateTime.now());

        ReviewEntity savedEntity = reviewRepository.save(entity);
        return buildFullResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        List<ReviewEntity> reviews = reviewRepository.findAllWithDetails();
        return reviews.stream()
                .map(reviewMapper::toDomain)
                .map(reviewMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewById(Long id) {
        ReviewEntity entity = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        return buildFullResponse(entity);
    }

    @Transactional
    public ReviewResponseDto updateReview(Long id, ReviewRequestDto requestDto) {
        ReviewEntity existingEntity = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        existingEntity.setRating(requestDto.getRating());
        existingEntity.setComment(requestDto.getComment());

        if (requestDto.getStudentId() != null &&
                !requestDto.getStudentId().equals(existingEntity.getStudent().getId())) {
            StudentEntity student = studentRepository.findById(requestDto.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException("Student not found"));
            existingEntity.setStudent(student);
        }

        if (requestDto.getTutorId() != null &&
                !requestDto.getTutorId().equals(existingEntity.getTutor().getId())) {
            TutorEntity tutor = tutorRepository.findById(requestDto.getTutorId())
                    .orElseThrow(() -> new EntityNotFoundException("Tutor not found"));
            existingEntity.setTutor(tutor);
        }

        ReviewEntity updatedEntity = reviewRepository.save(existingEntity);
        return buildFullResponse(updatedEntity);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByTutor(Long tutorId) {
        return reviewRepository.findByTutorId(tutorId).stream()
                .map(this::buildFullResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByStudent(Long studentId) {
        return reviewRepository.findByStudentId(studentId).stream()
                .map(this::buildFullResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponseDto buildFullResponse(ReviewEntity entity) {
        Review review = reviewMapper.toDomain(entity);
        return reviewMapper.toResponseDto(review);
    }
}