package io.buzzy.sso.authentication;

import io.buzzy.common.util.JsonUtil;
import io.buzzy.common.web.model.APIResponse;
import io.buzzy.common.web.model.ApiError;
import io.buzzy.common.web.model.ApiErrorCode;
import io.buzzy.sso.core.GlobalExceptionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.List;

public class ApiAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws ServletException {
        if (exception instanceof OAuth2AuthenticationException ex) {
            handleOAuth2AuthenticationException(response, ex);
        } else if (exception instanceof InsufficientAuthenticationException ex) {
            handleInsufficientAuthenticationException(response, ex);
        }
    }

    private void handleOAuth2AuthenticationException(HttpServletResponse response, OAuth2AuthenticationException ex) throws ServletException {
        ApiError error = new ApiError(
                ex.getError().getErrorCode().equals("invalid_scope") ?
                ApiErrorCode.InvalidScope : ApiErrorCode.UsernameOrPasswordIncorrect);

        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(JsonUtil.toJson(new APIResponse<>(null, List.of(error))));
        } catch (IOException e) {
            LOGGER.debug("Failed to write error response", e);
            rethrow(e);
        }
    }

    private void handleInsufficientAuthenticationException(HttpServletResponse response, AuthenticationException ex) throws ServletException {
        ApiError error = new ApiError(ApiErrorCode.Forbidden);

        try {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(JsonUtil.toJson(new APIResponse<>(null, List.of(error))));
        } catch (IOException e) {
            LOGGER.debug("Failed to write error response", e);
            rethrow(e);
        }
    }

    private void rethrow(Exception ex) throws ServletException {
        if (ex instanceof ServletException) {
            throw (ServletException) ex;
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new RuntimeException(ex);
    }
}
