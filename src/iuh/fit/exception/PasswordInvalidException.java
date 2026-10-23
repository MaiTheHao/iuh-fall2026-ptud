package iuh.fit.exception;

public class PasswordInvalidException extends AuthException {
    public PasswordInvalidException() {
    }

    public PasswordInvalidException(String message) {
        super(message);
    }
}
