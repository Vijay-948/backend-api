package backend.real_estate.backendapi.ExceptionHandling;

public class userNameNotFoundException extends Throwable {
    public userNameNotFoundException(String invalidUser) {
        super(invalidUser);
    }
}
