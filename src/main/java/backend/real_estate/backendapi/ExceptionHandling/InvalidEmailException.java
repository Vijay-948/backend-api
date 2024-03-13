package backend.real_estate.backendapi.ExceptionHandling;

public class InvalidEmailException extends Throwable {
    public InvalidEmailException(String message) {
        super(message);
    }
}
