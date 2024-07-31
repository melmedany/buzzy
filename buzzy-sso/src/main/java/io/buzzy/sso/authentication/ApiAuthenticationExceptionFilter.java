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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

public class ApiAuthenticationExceptionFilter extends GenericFilterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ResourceBundleMessagesService messageSource;

    public ApiAuthenticationExceptionFilter(ResourceBundleMessagesService messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException {
        try {
            chain.doFilter(request, response);
        } catch (UsernameNotFoundException ex) {
            handleUsernameNotFoundException((HttpServletRequest) request, (HttpServletResponse) response, ex);
        } catch (AccessDeniedException ex) {
            handleAccessDeniedException((HttpServletRequest) request, (HttpServletResponse) response, ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error", ex);
            handleGenericException((HttpServletRequest) request, (HttpServletResponse) response, ex);
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

    private void handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws ServletException {
        String errorMessage = messageSource.getMessage(ex.getMessage(), LocaleUtil.getRequestLocale(request));
        ApiError error = new ApiError(null, errorMessage);
        error.setCode(ApiErrorCode.Forbidden);

        try {
            LOGGER.error("Access denied", ex);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(JsonUtil.toJson(new APIResponse<>(null, List.of(error))));
        } catch (IOException e) {
            LOGGER.debug("Failed to write error response", e);
            rethrow(e);
        }
    }

    private void handleGenericException(HttpServletRequest request, HttpServletResponse response, Exception ex) throws ServletException {
        ApiError error = new ApiError(null, null);
        error.setCode(ApiErrorCode.Unknown);

        try {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
