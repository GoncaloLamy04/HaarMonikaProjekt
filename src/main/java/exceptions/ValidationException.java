package exceptions;

// Exceptions: Kastes når brugerinput er ugyldigt. fx tom email eller manglende felt.
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
