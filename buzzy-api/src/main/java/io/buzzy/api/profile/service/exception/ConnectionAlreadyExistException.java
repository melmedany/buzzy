package io.buzzy.api.profile.service.exception;

public class ConnectionAlreadyExistException extends RuntimeException {
    private final String username;
    private final String connection;

    public ConnectionAlreadyExistException(String username, String connection, String errorMessage) {
        super(errorMessage);
        this.username = username;
        this.connection = connection;
    }
}
