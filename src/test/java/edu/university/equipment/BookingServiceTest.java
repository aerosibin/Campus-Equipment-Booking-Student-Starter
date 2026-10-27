package edu.university.equipment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingServiceTest {

    private BookingService service;

    @BeforeEach
    void setup() {
        service = new BookingService();
    }

    @Test
    void successfulBookingReducesInventory() {
        Booking booking = service.create(
            "STU1001", "LAPTOP01", 2
        );

        assertEquals("CONFIRMED", booking.getStatus());
        assertEquals(2, booking.getQuantity());
        assertEquals(
            8,
            service.getEquipment("LAPTOP01").getAvailableQuantity()
        );
    }

    @Test
    void zeroQuantityRejected() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.create("STU1001", "LAPTOP01", 0)
        );

        assertEquals(
            10,
            service.getEquipment("LAPTOP01").getAvailableQuantity()
        );
        assertEquals(0, service.bookingCount());
    }

    @Test
    void blankBorrowerRejected() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.create(" ", "LAPTOP01", 1)
        );

        assertEquals(0, service.bookingCount());
    }

    @Test
    void unknownEquipmentRejected() {
        assertThrows(
            NoSuchElementException.class,
            () -> service.create("STU1001", "UNKNOWN", 1)
        );

        assertEquals(0, service.bookingCount());
    }

    @Test
    void overCapacityBookingIsRejectedWithoutChangingInventory() {
        // Only one projector is available.
        service.getEquipment("PROJECTOR01").setAvailableQuantity(1);

        assertThrows(
            IllegalStateException.class,
            () -> service.create("STU1001", "PROJECTOR01", 2)
        );

        assertEquals(
            1,
            service.getEquipment("PROJECTOR01").getAvailableQuantity()
        );
        assertEquals(0, service.bookingCount());
    }

    @Test
    void bookingExactlyAvailableQuantityIsAccepted() {
        service.getEquipment("PROJECTOR01").setAvailableQuantity(2);

        Booking booking = service.create(
            "STU1001", "PROJECTOR01", 2
        );

        assertEquals("CONFIRMED", booking.getStatus());
        assertEquals(0,
            service.getEquipment("PROJECTOR01").getAvailableQuantity());
        assertEquals(1, service.bookingCount());
    }

    @Test
    void bookingOfOneUnitIsAccepted() {
        Booking booking = service.create(
            "STU1001", "LAPTOP01", 1
        );

        assertEquals(1, booking.getQuantity());
        assertEquals(
            9,
            service.getEquipment("LAPTOP01").getAvailableQuantity()
        );
    }

    @Test
    void bookingOfExactlyTwoUnitsIsAccepted() {
        Booking booking = service.create(
            "STU1001", "LAPTOP01", 2
        );

        assertEquals(2, booking.getQuantity());
        assertEquals(
            8,
            service.getEquipment("LAPTOP01").getAvailableQuantity()
        );
        assertEquals(1, service.bookingCount());
    }

    @Test
    void bookingOfThreeUnitsIsRejectedWithoutChangingInventory() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.create("STU1001", "LAPTOP01", 3)
        );

        assertEquals(
            10,
            service.getEquipment("LAPTOP01").getAvailableQuantity()
        );
        assertEquals(0, service.bookingCount());
    }

    @Test
    void cancellationRestoresInventoryOnce() {
        Booking booking = service.create(
            "STU1001", "TABLET01", 2
        );

        service.cancel(booking.getId());

        assertEquals("CANCELLED", booking.getStatus());
        assertEquals(
            8,
            service.getEquipment("TABLET01").getAvailableQuantity()
        );

        assertThrows(
            IllegalStateException.class,
            () -> service.cancel(booking.getId())
        );

        assertEquals(
            8,
            service.getEquipment("TABLET01").getAvailableQuantity()
        );
    }
}