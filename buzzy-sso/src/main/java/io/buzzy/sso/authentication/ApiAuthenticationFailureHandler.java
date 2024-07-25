package io.buzzy.sso.authentication;

import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.common.util.JsonUtil;
import io.buzzy.common.util.LocaleUtil;
import io.buzzy.common.web.model.APIResponse;
import io.buzzy.common.web.model.ApiError;
import io.buzzy.common.web.model.ApiErrorCode;
import io.buzzy.sso.core.GlobalExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

public class ApiAuthenticationFailureHandler extends GenericFilterBean implements AuthenticationFailureHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ResourceBundleMessagesService messageSource;

    public ApiAuthenticationFailureHandler(ResourceBundleMessagesService messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            if (ex instanceof UsernameNotFoundException) {
                handleUsernameNotFoundException((HttpServletRequest) request, (HttpServletResponse) response, (UsernameNotFoundException) ex);
            }
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws ServletException {
        if (exception instanceof OAuth2AuthenticationException) {
            handleOAuth2AuthenticationException(response);
        }
    }

    private void handleUsernameNotFoundException(HttpServletRequest request, HttpServletResponse response, UsernameNotFoundException ex) throws ServletException {
        String errorMessage = messageSource.getMessage(ex.getMessage(), LocaleUtil.getRequestLocale(request));
        ApiError error = new ApiError(null, errorMessage);
        error.setCode(ApiErrorCode.UsernameNotFound);

        try {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(JsonUtil.toJson(new APIResponse<>(null, List.of(error))));
        } catch (IOException e) {
            LOGGER.debug("Failed to write error response", e);
            rethrow(e);
        }
    }

    private void handleOAuth2AuthenticationException(HttpServletResponse response) throws ServletException {
        ApiError error = new ApiError(ApiErrorCode.UsernameOrPasswordIncorrect);

        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
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
