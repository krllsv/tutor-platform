package krllsv.tutor.api.mapper;

import krllsv.tutor.api.domain.Booking;
import krllsv.tutor.api.dto.request.BookingRequestDto;
import krllsv.tutor.api.dto.response.BookingResponseDto;
import krllsv.tutor.api.entity.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingEntity toEntity(BookingRequestDto dto) {
        if (dto == null) {
            return null;
        }

        BookingEntity entity = new BookingEntity();
        entity.setDateTime(dto.getDateTime());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setMessage(dto.getMessage());

        return entity;
    }

    public Booking toDomain(BookingEntity entity) {
        if (entity == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setId(entity.getId());
        booking.setDateTime(entity.getDateTime());
        booking.setDurationMinutes(entity.getDurationMinutes());
        booking.setStatus(entity.getStatus());
        booking.setMessage(entity.getMessage());

        if (entity.getStudent() != null) {
            booking.setStudentId(entity.getStudent().getId());
        }

        if (entity.getTutor() != null) {
            booking.setTutorId(entity.getTutor().getId());
        }

        return booking;
    }

    public BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setDateTime(booking.getDateTime());
        dto.setDurationMinutes(booking.getDurationMinutes());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setMessage(booking.getMessage());

        dto.setStudentId(booking.getStudentId());
        dto.setTutorId(booking.getTutorId());

        return dto;
    }
}