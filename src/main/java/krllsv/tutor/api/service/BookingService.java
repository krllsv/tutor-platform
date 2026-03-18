package krllsv.tutor.api.service;

import jakarta.persistence.EntityNotFoundException;
import krllsv.tutor.api.domain.Booking;
import krllsv.tutor.api.dto.request.BookingRequestDto;
import krllsv.tutor.api.dto.response.BookingResponseDto;
import krllsv.tutor.api.entity.BookingEntity;
import krllsv.tutor.api.entity.StudentEntity;
import krllsv.tutor.api.entity.TutorEntity;
import krllsv.tutor.api.mapper.BookingMapper;
import krllsv.tutor.api.repository.BookingRepository;
import krllsv.tutor.api.repository.StudentRepository;
import krllsv.tutor.api.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private static final String BOOKING_NOT_FOUND = "Booking not found with id: ";

    private final BookingRepository bookingRepository;
    private final StudentRepository studentRepository;
    private final TutorRepository tutorRepository;
    private final BookingMapper bookingMapper;

    public BookingService(BookingRepository bookingRepository,
                          StudentRepository studentRepository,
                          TutorRepository tutorRepository,
                          BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto requestDto) {

        StudentEntity student = studentRepository.findById(requestDto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " +
                        requestDto.getStudentId()));

        TutorEntity tutor = tutorRepository.findById(requestDto.getTutorId())
                .orElseThrow(() -> new EntityNotFoundException("Tutor not found with id: " +
                        requestDto.getTutorId()));

        LocalDateTime newStart = requestDto.getDateTime();
        LocalDateTime newEnd = newStart.plusMinutes(requestDto.getDurationMinutes());

        List<BookingEntity> existingBookings = bookingRepository
                .findByTutorIdAndStatusIn(requestDto.getTutorId(),
                        List.of("PENDING", "CONFIRMED"));

        boolean hasOverlap = existingBookings.stream().anyMatch(booking -> {
            LocalDateTime existingStart = booking.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(booking.getDurationMinutes());
            return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
        });

        if (hasOverlap) {
            throw new IllegalStateException("Tutor is already booked during this time interval");
        }

        BookingEntity entity = bookingMapper.toEntity(requestDto);
        entity.setStudent(student);
        entity.setTutor(tutor);
        entity.setStatus("PENDING");

        BookingEntity savedEntity = bookingRepository.save(entity);
        return buildFullResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {
        List<BookingEntity> bookings = bookingRepository.findAllWithDetails();
        return bookings.stream()
                .map(this::buildFullResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long id) {
        BookingEntity entity = bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException(BOOKING_NOT_FOUND + id));
        return buildFullResponse(entity);
    }

    @Transactional
    public BookingResponseDto updateBooking(Long id, BookingRequestDto requestDto) {
        BookingEntity existingEntity = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BOOKING_NOT_FOUND + id));

        existingEntity.setDateTime(requestDto.getDateTime());
        existingEntity.setDurationMinutes(requestDto.getDurationMinutes());
        existingEntity.setMessage(requestDto.getMessage());

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

        BookingEntity updatedEntity = bookingRepository.save(existingEntity);
        return buildFullResponse(updatedEntity);
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException(BOOKING_NOT_FOUND + id);
        }
        bookingRepository.deleteById(id);
    }

    @Transactional
    public BookingResponseDto changeStatus(Long id, String newStatus) {
        BookingEntity entity = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BOOKING_NOT_FOUND + id));

        if (!List.of("CONFIRMED", "CANCELLED", "COMPLETED").contains(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        entity.setStatus(newStatus);
        return buildFullResponse(bookingRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByTutor(Long tutorId) {
        return bookingRepository.findByTutorId(tutorId).stream()
                .map(this::buildFullResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByStudent(Long studentId) {
        return bookingRepository.findByStudentId(studentId).stream()
                .map(this::buildFullResponse)
                .toList();
    }

    private BookingResponseDto buildFullResponse(BookingEntity entity) {
        Booking booking = bookingMapper.toDomain(entity);
        BookingResponseDto dto = bookingMapper.toResponseDto(booking);
        if (entity.getStudent() != null) {
            dto.setStudentName(entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName());
        }

        if (entity.getTutor() != null) {
            dto.setTutorName(entity.getTutor().getFirstName() + " " + entity.getTutor().getLastName());
        }

        return dto;
    }
}