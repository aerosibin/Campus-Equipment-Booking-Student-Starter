package edu.university.equipment;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {

    private final BookingService service;

    public ApiController(BookingService service) {
        this.service = service;
    }

    @GetMapping("/api/equipment")
    public List<Equipment> equipment() {
        return service.listEquipment();
    }

    @GetMapping("/api/equipment/{id}")
    public Equipment equipment(@PathVariable String id) {
        return service.getEquipment(id);
    }

    @PostMapping("/api/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(
            @Valid @RequestBody CreateBookingRequest request) {

        return service.create(
            request.getBorrowerId(),
            request.getEquipmentId(),
            request.getQuantity()
        );
    }

    @GetMapping("/api/bookings/{id}")
    public Booking booking(@PathVariable String id) {
        return service.getBooking(id);
    }

    @PostMapping("/api/bookings/{id}/cancel")
    public Booking cancel(@PathVariable String id) {
        return service.cancel(id);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(Exception exception) {
        return Map.of(
            "error", "NOT_FOUND",
            "message", exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(Exception exception) {
        return Map.of(
            "error", "BAD_REQUEST",
            "message", exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict(Exception exception) {
        return Map.of(
            "error", "CONFLICT",
            "message", exception.getMessage()
        );
    }
}