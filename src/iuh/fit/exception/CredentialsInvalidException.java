package iuh.fit.exception;

public class CredentialsInvalidException extends AuthException {
    public CredentialsInvalidException() {
    }

    public CredentialsInvalidException(String message) {
        super(message);
    }
}
