package exceptions;

// Exceptions: Kastes når der opstår en fejl i databaselaget (wrapper omkring SQLException).
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}