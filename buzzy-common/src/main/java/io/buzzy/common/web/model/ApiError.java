package io.buzzy.common.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private String field;
    private String message;
    private ApiErrorCode code;

    public ApiError() {
    }

    public ApiError(ApiErrorCode code) {
        this.code = code;
    }

    public ApiError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiErrorCode getCode() {
        return code;
    }

    public void setCode(ApiErrorCode code) {
        this.code = code;
    }
}
