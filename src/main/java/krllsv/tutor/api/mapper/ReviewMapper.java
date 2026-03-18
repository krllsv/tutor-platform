package krllsv.tutor.api.mapper;

import krllsv.tutor.api.domain.Review;
import krllsv.tutor.api.dto.request.ReviewRequestDto;
import krllsv.tutor.api.dto.response.ReviewResponseDto;
import krllsv.tutor.api.entity.ReviewEntity;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewEntity toEntity(ReviewRequestDto dto) {
        if (dto == null) {
            return null;
        }

        ReviewEntity entity = new ReviewEntity();
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());
        return entity;
    }

    public Review toDomain(ReviewEntity entity) {
        if (entity == null) {
            return null;
        }

        Review review = new Review();
        review.setId(entity.getId());
        review.setRating(entity.getRating());
        review.setComment(entity.getComment());
        review.setCreatedAt(entity.getCreatedAt());

        if (entity.getStudent() != null) {
            review.setStudentId(entity.getStudent().getId());
            review.setStudentName(entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName());
        }

        if (entity.getTutor() != null) {
            review.setTutorId(entity.getTutor().getId());
            review.setTutorName(entity.getTutor().getFirstName() + " " + entity.getTutor().getLastName());
        }
        return review;
    }

    public ReviewResponseDto toResponseDto(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        dto.setStudentId(review.getStudentId());
        dto.setStudentName(review.getStudentName());
        dto.setTutorId(review.getTutorId());
        dto.setTutorName(review.getTutorName());

        return dto;
    }
}