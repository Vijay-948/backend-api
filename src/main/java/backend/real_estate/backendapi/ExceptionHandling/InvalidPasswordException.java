package backend.real_estate.backendapi.ExceptionHandling;

public class InvalidPasswordException extends Throwable {
    public InvalidPasswordException(String invalid_password_format) {
        super(invalid_password_format);
    }
}
