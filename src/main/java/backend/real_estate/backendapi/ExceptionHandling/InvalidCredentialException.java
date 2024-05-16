package backend.real_estate.backendapi.ExceptionHandling;

public class InvalidCredentialException extends RuntimeException{

    private String message;

    public InvalidCredentialException(String message){
        super(message);
    }
}
