package backend.real_estate.backendapi.ExceptionHandling;

public class EmailAlreadyExistException extends Throwable {
    public EmailAlreadyExistException(String emailIsAlreadyExistsMessage) {
        super(emailIsAlreadyExistsMessage);
    }
}
