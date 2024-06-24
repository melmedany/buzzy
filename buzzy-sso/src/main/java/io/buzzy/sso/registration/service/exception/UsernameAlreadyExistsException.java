package io.buzzy.sso.registration.service.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

}
