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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDto requestDto;
    private BookingEntity bookingEntity;
    private BookingEntity savedBookingEntity;
    private StudentEntity studentEntity;
    private TutorEntity tutorEntity;
    private Booking domainBooking;
    private BookingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new BookingRequestDto();
        requestDto.setDateTime(LocalDateTime.of(2024, 12, 20, 15, 0));
        requestDto.setDurationMinutes(60);
        requestDto.setStudentId(1L);
        requestDto.setTutorId(1L);
        requestDto.setMessage("Test booking");

        studentEntity = new StudentEntity();
        studentEntity.setId(1L);
        studentEntity.setFirstName("Иван");
        studentEntity.setLastName("Петров");

        tutorEntity = new TutorEntity();
        tutorEntity.setId(1L);
        tutorEntity.setFirstName("Петр");
        tutorEntity.setLastName("Иванов");

        bookingEntity = new BookingEntity();
        bookingEntity.setDateTime(requestDto.getDateTime());
        bookingEntity.setDurationMinutes(60);

        savedBookingEntity = new BookingEntity();
        savedBookingEntity.setId(1L);
        savedBookingEntity.setDateTime(requestDto.getDateTime());
        savedBookingEntity.setDurationMinutes(60);
        savedBookingEntity.setStatus("PENDING");
        savedBookingEntity.setStudent(studentEntity);
        savedBookingEntity.setTutor(tutorEntity);

        domainBooking = new Booking();
        domainBooking.setId(1L);
        domainBooking.setStatus("PENDING");

        responseDto = new BookingResponseDto();
        responseDto.setId(1L);
        responseDto.setStatus("PENDING");
        responseDto.setStudentName("Иван Петров");
        responseDto.setTutorName("Петр Иванов");
    }

    @Test
    void createBooking_ShouldSaveBooking_WhenValidData() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorEntity));
        when(bookingRepository.findByTutorIdAndStatusIn(anyLong(), anyList())).thenReturn(List.of());
        when(bookingMapper.toEntity(requestDto)).thenReturn(bookingEntity);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBookingEntity);
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.createBooking(requestDto);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void createBooking_ShouldThrowException_WhenStudentNotFound() {
        Long invalidStudentId = 999L;
        when(studentRepository.findById(invalidStudentId)).thenReturn(Optional.empty());

        BookingRequestDto invalidRequest = new BookingRequestDto();
        invalidRequest.setDateTime(LocalDateTime.of(2024, 12, 20, 15, 0));
        invalidRequest.setDurationMinutes(60);
        invalidRequest.setStudentId(invalidStudentId);
        invalidRequest.setTutorId(1L);

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(invalidRequest));
        verify(tutorRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(BookingEntity.class));
    }

    @Test
    void createBooking_ShouldThrowException_WhenTutorNotFound() {
        Long invalidTutorId = 999L;
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));
        when(tutorRepository.findById(invalidTutorId)).thenReturn(Optional.empty());

        BookingRequestDto invalidRequest = new BookingRequestDto();
        invalidRequest.setDateTime(LocalDateTime.of(2024, 12, 20, 15, 0));
        invalidRequest.setDurationMinutes(60);
        invalidRequest.setStudentId(1L);
        invalidRequest.setTutorId(invalidTutorId);

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(invalidRequest));
        verify(bookingRepository, never()).save(any(BookingEntity.class));
    }

    @Test
    void createBooking_ShouldThrowException_WhenTimeOverlap() {
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 20, 15, 0);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorEntity));

        BookingEntity existingBooking = new BookingEntity();
        existingBooking.setDateTime(startTime);
        existingBooking.setDurationMinutes(60);

        when(bookingRepository.findByTutorIdAndStatusIn(anyLong(), anyList()))
                .thenReturn(List.of(existingBooking));

        BookingRequestDto overlapRequest = new BookingRequestDto();
        overlapRequest.setDateTime(startTime);
        overlapRequest.setDurationMinutes(60);
        overlapRequest.setStudentId(1L);
        overlapRequest.setTutorId(1L);

        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(overlapRequest));
        verify(bookingRepository, never()).save(any(BookingEntity.class));
    }

    @Test
    void createBooking_ShouldNotThrowOverlap_WhenNoOverlap() {
        LocalDateTime existingStart = LocalDateTime.of(2024, 12, 20, 14, 0);
        LocalDateTime newStart = LocalDateTime.of(2024, 12, 20, 15, 0);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorEntity));

        BookingEntity existingBooking = new BookingEntity();
        existingBooking.setDateTime(existingStart);
        existingBooking.setDurationMinutes(60);

        when(bookingRepository.findByTutorIdAndStatusIn(anyLong(), anyList()))
                .thenReturn(List.of(existingBooking));

        BookingRequestDto noOverlapRequest = new BookingRequestDto();
        noOverlapRequest.setDateTime(newStart);
        noOverlapRequest.setDurationMinutes(60);
        noOverlapRequest.setStudentId(1L);
        noOverlapRequest.setTutorId(1L);

        when(bookingMapper.toEntity(noOverlapRequest)).thenReturn(bookingEntity);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBookingEntity);
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.createBooking(noOverlapRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void createBooking_ShouldNotThrowOverlap_WhenNewEndIsBeforeExistingEnd() {
        LocalDateTime existingStart = LocalDateTime.of(2024, 12, 20, 14, 0);
        LocalDateTime newStart = LocalDateTime.of(2024, 12, 20, 13, 0);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorEntity));

        BookingEntity existingBooking = new BookingEntity();
        existingBooking.setDateTime(existingStart);
        existingBooking.setDurationMinutes(120);

        when(bookingRepository.findByTutorIdAndStatusIn(anyLong(), anyList()))
                .thenReturn(List.of(existingBooking));

        BookingRequestDto noOverlapRequest = new BookingRequestDto();
        noOverlapRequest.setDateTime(newStart);
        noOverlapRequest.setDurationMinutes(30);
        noOverlapRequest.setStudentId(1L);
        noOverlapRequest.setTutorId(1L);

        when(bookingMapper.toEntity(noOverlapRequest)).thenReturn(bookingEntity);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBookingEntity);
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.createBooking(noOverlapRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void getBookingById_ShouldReturnBooking_WhenExists() {
        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(savedBookingEntity));
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBookingById_ShouldThrowException_WhenNotFound() {
        when(bookingRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingById(999L));
    }

    @Test
    void getAllBookings_ShouldReturnList() {
        when(bookingRepository.findAllWithDetails()).thenReturn(List.of(savedBookingEntity));
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getAllBookings();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllWithDetails();
    }

    @Test
    void getAllBookings_ShouldReturnEmptyList_WhenNoBookings() {
        when(bookingRepository.findAllWithDetails()).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.getAllBookings();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getBookingsByTutor_ShouldReturnList() {
        Long tutorId = 1L;
        when(bookingRepository.findByTutorId(tutorId)).thenReturn(List.of(savedBookingEntity));
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getBookingsByTutor(tutorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findByTutorId(tutorId);
    }

    @Test
    void getBookingsByStudent_ShouldReturnList() {
        Long studentId = 1L;
        when(bookingRepository.findByStudentId(studentId)).thenReturn(List.of(savedBookingEntity));
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getBookingsByStudent(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findByStudentId(studentId);
    }

    @Test
    void updateBooking_ShouldUpdate_WhenExists() {
        Long id = 1L;
        BookingRequestDto updateRequest = new BookingRequestDto();
        updateRequest.setDateTime(LocalDateTime.of(2024, 12, 21, 16, 0));
        updateRequest.setDurationMinutes(90);
        updateRequest.setMessage("Updated message");
        updateRequest.setStudentId(1L);
        updateRequest.setTutorId(1L);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(savedBookingEntity));
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBookingEntity);
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.updateBooking(id, updateRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void updateBooking_ShouldThrowException_WhenNotFound() {
        Long id = 999L;
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.updateBooking(id, requestDto));
    }

    @Test
    void updateBooking_ShouldUpdateStudent_WhenStudentIdChanged() {
        Long id = 1L;
        Long newStudentId = 2L;

        BookingRequestDto updateRequest = new BookingRequestDto();
        updateRequest.setDateTime(LocalDateTime.of(2024, 12, 21, 16, 0));
        updateRequest.setDurationMinutes(90);
        updateRequest.setStudentId(newStudentId);
        updateRequest.setTutorId(1L);

        StudentEntity newStudent = new StudentEntity();
        newStudent.setId(newStudentId);
        newStudent.setFirstName("Новый");

        when(bookingRepository.findById(id)).thenReturn(Optional.of(savedBookingEntity));
        when(studentRepository.findById(newStudentId)).thenReturn(Optional.of(newStudent));
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBookingEntity);
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.updateBooking(id, updateRequest);

        assertNotNull(result);
        verify(studentRepository).findById(newStudentId);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void updateBooking_ShouldUpdateTutor_WhenTutorIdChanged() {
        Long id = 1L;
        Long newTutorId = 2L;

        BookingRequestDto updateRequest = new BookingRequestDto();
        updateRequest.setDateTime(LocalDateTime.of(2024, 12, 21, 16, 0));
        updateRequest.setDurationMinutes(90);
        updateRequest.setStudentId(1L);
        updateRequest.setTutorId(newTutorId);

        TutorEntity newTutor = new TutorEntity();
        newTutor.setId(newTutorId);
        newTutor.setFirstName("Новый");

        when(bookingRepository.findById(id)).thenReturn(Optional.of(savedBookingEntity));
        when(tutorRepository.findById(newTutorId)).thenReturn(Optional.of(newTutor));
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBookingEntity);
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.updateBooking(id, updateRequest);

        assertNotNull(result);
        verify(tutorRepository).findById(newTutorId);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void updateBooking_ShouldNotUpdateStudentOrTutor_WhenIdsAreNull() {
        Long id = 1L;
        BookingRequestDto updateRequest = new BookingRequestDto();
        updateRequest.setDateTime(LocalDateTime.of(2024, 12, 21, 16, 0));
        updateRequest.setDurationMinutes(90);
        updateRequest.setStudentId(null);
        updateRequest.setTutorId(null);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(savedBookingEntity));
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBookingEntity);
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.updateBooking(id, updateRequest);

        assertNotNull(result);
        verify(studentRepository, never()).findById(anyLong());
        verify(tutorRepository, never()).findById(anyLong());
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void updateBooking_ShouldNotUpdateStudentOrTutor_WhenIdsSame() {
        Long id = 1L;
        BookingRequestDto updateRequest = new BookingRequestDto();
        updateRequest.setDateTime(LocalDateTime.of(2024, 12, 21, 16, 0));
        updateRequest.setDurationMinutes(90);
        updateRequest.setStudentId(1L);
        updateRequest.setTutorId(1L);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(savedBookingEntity));
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBookingEntity);
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.updateBooking(id, updateRequest);

        assertNotNull(result);
        verify(studentRepository, never()).findById(anyLong());
        verify(tutorRepository, never()).findById(anyLong());
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void deleteBooking_ShouldDelete_WhenExists() {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookingRepository).deleteById(1L);

        bookingService.deleteBooking(1L);

        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void deleteBooking_ShouldThrowException_WhenNotFound() {
        when(bookingRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookingService.deleteBooking(999L));
        verify(bookingRepository, never()).deleteById(anyLong());
    }

    @Test
    void changeStatus_ShouldUpdateStatus_WhenValidStatus() {
        Long id = 1L;
        String newStatus = "CONFIRMED";

        when(bookingRepository.findById(id)).thenReturn(Optional.of(savedBookingEntity));
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBookingEntity);
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.changeStatus(id, newStatus);

        assertNotNull(result);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void changeStatus_ShouldThrowException_WhenInvalidStatus() {
        Long id = 1L;
        String invalidStatus = "INVALID";

        when(bookingRepository.findById(id)).thenReturn(Optional.of(savedBookingEntity));

        assertThrows(IllegalArgumentException.class, () -> bookingService.changeStatus(id, invalidStatus));
        verify(bookingRepository, never()).save(any(BookingEntity.class));
    }

    @Test
    void changeStatus_ShouldThrowException_WhenEmptyStatus() {
        Long id = 1L;
        String emptyStatus = "";

        when(bookingRepository.findById(id)).thenReturn(Optional.of(savedBookingEntity));

        assertThrows(IllegalArgumentException.class, () -> bookingService.changeStatus(id, emptyStatus));
    }

    @Test
    void changeStatus_ShouldThrowException_WhenBookingNotFound() {
        Long id = 999L;
        String newStatus = "CONFIRMED";

        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.changeStatus(id, newStatus));
        verify(bookingRepository, never()).save(any(BookingEntity.class));
    }

    @Test
    void buildFullResponse_ShouldHandleNullStudent() {
        BookingEntity bookingWithoutStudent = new BookingEntity();
        bookingWithoutStudent.setId(2L);
        bookingWithoutStudent.setTutor(tutorEntity);
        bookingWithoutStudent.setStudent(null);

        when(bookingRepository.findByIdWithDetails(2L)).thenReturn(Optional.of(bookingWithoutStudent));
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.getBookingById(2L);

        assertNotNull(result);
        verify(bookingMapper).toResponseDto(any(Booking.class));
    }

    @Test
    void buildFullResponse_ShouldHandleNullTutor() {
        BookingEntity bookingWithoutTutor = new BookingEntity();
        bookingWithoutTutor.setId(3L);
        bookingWithoutTutor.setStudent(studentEntity);
        bookingWithoutTutor.setTutor(null);

        when(bookingRepository.findByIdWithDetails(3L)).thenReturn(Optional.of(bookingWithoutTutor));
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.getBookingById(3L);

        assertNotNull(result);
        verify(bookingMapper).toResponseDto(any(Booking.class));
    }

    @Test
    void buildFullResponse_ShouldHandleNullStudentAndNullTutor() {
        BookingEntity bookingWithoutBoth = new BookingEntity();
        bookingWithoutBoth.setId(4L);
        bookingWithoutBoth.setStudent(null);
        bookingWithoutBoth.setTutor(null);

        when(bookingRepository.findByIdWithDetails(4L)).thenReturn(Optional.of(bookingWithoutBoth));
        when(bookingMapper.toDomain(any(BookingEntity.class))).thenReturn(domainBooking);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        BookingResponseDto result = bookingService.getBookingById(4L);

        assertNotNull(result);
        verify(bookingMapper).toResponseDto(any(Booking.class));
    }
}