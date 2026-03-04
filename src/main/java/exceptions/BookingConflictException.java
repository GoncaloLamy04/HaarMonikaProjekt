package exceptions;

// Exceptions: Kastes når en booking overlapper med en eksisterende booking for medarbejder eller kunde.
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}