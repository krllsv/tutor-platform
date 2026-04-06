package krllsv.tutor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Booking Controller", description = "Управление бронированиями")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Создать бронирование", description = "Создаёт новое бронирование занятия")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Бронирование создано"),
                            @ApiResponse(responseCode = "400", description = "Некорректные данные или время занято"),
                            @ApiResponse(responseCode = "404", description = "Студент или преподаватель не найден")
    })
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto requestDto) {
        BookingResponseDto created = bookingService.createBooking(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Получить все бронирования", description = "Возвращает список всех бронирований")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        List<BookingResponseDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить бронирование по ID",
            description = "Возвращает бронирование по указанному идентификатору")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно"),
                            @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    })
    public ResponseEntity<BookingResponseDto> getBookingById(
            @Parameter(description = "ID бронирования", example = "1") @PathVariable Long id
    ) {
        BookingResponseDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить бронирование", description = "Обновляет данные бронирования по ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Успешно обновлено"),
                            @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    })
    public ResponseEntity<BookingResponseDto> updateBooking(
            @Parameter(description = "ID бронирования", example = "1") @PathVariable Long id,
            @RequestBody BookingRequestDto requestDto
    ) {
        BookingResponseDto updated = bookingService.updateBooking(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить бронирование", description = "Удаляет бронирование по ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Успешно удалено"),
                            @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    })
    public ResponseEntity<Void> deleteBooking(
            @Parameter(description = "ID бронирования", example = "1") @PathVariable Long id
    ) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Изменить статус бронирования",
            description = "Изменяет статус бронирования (PENDING, CONFIRMED, CANCELLED, COMPLETED)")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Статус обновлён"),
                            @ApiResponse(responseCode = "404", description = "Бронирование не найдено"),
                            @ApiResponse(responseCode = "400", description = "Некорректный статус")
    })
    public ResponseEntity<BookingResponseDto> changeStatus(
            @Parameter(description = "ID бронирования", example = "1") @PathVariable Long id,
            @Parameter(description = "Новый статус", example = "CONFIRMED") @RequestParam String status
    ) {
        BookingResponseDto updated = bookingService.changeStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/by-tutor/{tutorId}")
    @Operation(summary = "Получить бронирования преподавателя",
            description = "Возвращает все бронирования указанного преподавателя")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByTutor(
            @Parameter(description = "ID преподавателя", example = "1") @PathVariable Long tutorId
    ) {
        List<BookingResponseDto> bookings = bookingService.getBookingsByTutor(tutorId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/by-student/{studentId}")
    @Operation(summary = "Получить бронирования студента",
            description = "Возвращает все бронирования указанного студента")
    @ApiResponse(responseCode = "200", description = "Успешно")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByStudent(
            @Parameter(description = "ID студента", example = "1") @PathVariable Long studentId
    ) {
        List<BookingResponseDto> bookings = bookingService.getBookingsByStudent(studentId);
        return ResponseEntity.ok(bookings);
    }
}