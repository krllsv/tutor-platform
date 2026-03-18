package krllsv.tutor.api.controller;

import krllsv.tutor.api.dto.request.BookingRequestDto;
import krllsv.tutor.api.dto.response.BookingResponseDto;
import krllsv.tutor.api.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto requestDto) {
        BookingResponseDto created = bookingService.createBooking(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        List<BookingResponseDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long id) {
        BookingResponseDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDto> updateBooking(
            @PathVariable Long id,
            @RequestBody BookingRequestDto requestDto) {
        BookingResponseDto updated = bookingService.updateBooking(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDto> changeStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        BookingResponseDto updated = bookingService.changeStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/by-tutor/{tutorId}")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByTutor(@PathVariable Long tutorId) {
        List<BookingResponseDto> bookings = bookingService.getBookingsByTutor(tutorId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByStudent(@PathVariable Long studentId) {
        List<BookingResponseDto> bookings = bookingService.getBookingsByStudent(studentId);
        return ResponseEntity.ok(bookings);
    }
}