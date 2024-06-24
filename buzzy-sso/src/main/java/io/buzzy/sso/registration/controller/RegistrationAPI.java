package io.buzzy.sso.registration.controller;

import io.buzzy.common.web.model.APIResponse;
import io.buzzy.sso.registration.controller.model.SignupRequest;
import io.buzzy.sso.registration.controller.model.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "RegistrationAPI")
public interface RegistrationAPI {

    @Operation(method = "post", operationId = "signup", summary = "Create new user account",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = SignupRequest.class), mediaType = "application/json")),
            responses = {@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(schema = @Schema(implementation = UserDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @PostMapping(value = "signup", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    ResponseEntity<APIResponse> signup(@Valid @RequestBody SignupRequest registrationRequest);



}
