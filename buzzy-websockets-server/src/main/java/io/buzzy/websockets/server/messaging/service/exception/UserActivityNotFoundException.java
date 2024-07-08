package io.buzzy.websockets.server.messaging.service.exception;

public class UserActivityNotFoundException extends RuntimeException {

    public UserActivityNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}