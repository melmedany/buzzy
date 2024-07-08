package io.buzzy.common.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
    private T data;
    private List<ApiError> errors;

    public APIResponse(T data, List<ApiError> errors) {
        this.data = data;
        this.errors = errors;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<ApiError> getErrors() {
        return errors;
    }

    public void setErrors(List<ApiError> errors) {
        this.errors = errors;
    }

    public static <T> APIResponse<T> emptyResponse() {
        return new <T>APIResponse<T>(null, null);
    }
}
