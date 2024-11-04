package io.buzzy.api.profile.service.exception;

import java.util.UUID;

public class ConnectionAlreadyExistException extends RuntimeException {
    private final UUID username;
    private final UUID connection;

    public ConnectionAlreadyExistException(UUID username, UUID connection, String errorMessage) {
        super(errorMessage);
        this.username = username;
        this.connection = connection;
    }

    public UUID getUsername() {
        return username;
    }

    public UUID getConnection() {
        return connection;
    }
}
