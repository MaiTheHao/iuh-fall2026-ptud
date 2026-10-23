package iuh.fit.exception;

public class UserNotFoundException extends AuthException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
