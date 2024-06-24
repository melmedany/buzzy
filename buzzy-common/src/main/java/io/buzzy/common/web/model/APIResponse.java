package io.buzzy.common.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record APIResponse(Object data, List<ApiError> errors) {
}
