package edu.university.equipment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private static final int MAX_BOOKING_QUANTITY = 2;

    private final Map<String, Equipment> equipment =
            new LinkedHashMap<>();

    private final Map<String, Booking> bookings =
            new LinkedHashMap<>();

    public BookingService() {
        equipment.put(
            "LAPTOP01",
            new Equipment("LAPTOP01", "Laptop", "COMPUTING", 10)
        );

        equipment.put(
            "PROJECTOR01",
            new Equipment("PROJECTOR01", "Projector", "PRESENTATION", 4)
        );

        equipment.put(
            "TABLET01",
            new Equipment("TABLET01", "Tablet", "COMPUTING", 8)
        );
    }

    public synchronized List<Equipment> listEquipment() {
        return new ArrayList<>(equipment.values());
    }

    public synchronized Equipment getEquipment(String id) {
        Equipment item = equipment.get(id);

        if (item == null) {
            throw new NoSuchElementException(
                "Equipment not found: " + id
            );
        }

        return item;
    }

    public synchronized Booking create(
            String borrower,
            String equipmentId,
            int quantity) {

        if (borrower == null || borrower.isBlank()) {
            throw new IllegalArgumentException(
                "borrowerId must not be blank"
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "quantity must be positive"
            );
        }

        Equipment item = getEquipment(equipmentId);

        // Task 4: enforce the per-booking quantity limit.
        if (quantity > MAX_BOOKING_QUANTITY) {
            throw new IllegalArgumentException(
                "A booking may contain at most "
                + MAX_BOOKING_QUANTITY + " units"
            );
        }

        // Task 3: reject requests exceeding inventory.
        if (quantity > item.getAvailableQuantity()) {
            throw new IllegalStateException(
                "Requested quantity exceeds available inventory"
            );
        }

        // Only modify state after every validation succeeds.
        item.setAvailableQuantity(
            item.getAvailableQuantity() - quantity
        );

        Booking booking = new Booking(
            UUID.randomUUID().toString(),
            borrower,
            equipmentId,
            quantity
        );

        bookings.put(booking.getId(), booking);

        return booking;
    }

    public synchronized Booking getBooking(String id) {
        Booking booking = bookings.get(id);

        if (booking == null) {
            throw new NoSuchElementException(
                "Booking not found: " + id
            );
        }

        return booking;
    }

    public synchronized Booking cancel(String id) {
        Booking booking = getBooking(id);

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new IllegalStateException(
                "Booking already cancelled"
            );
        }

        Equipment item = getEquipment(booking.getEquipmentId());

        item.setAvailableQuantity(
            item.getAvailableQuantity() + booking.getQuantity()
        );

        booking.setStatus("CANCELLED");

        return booking;
    }

    // Used by unit tests to verify rejected bookings create no record.
    synchronized int bookingCount() {
        return bookings.size();
    }
}