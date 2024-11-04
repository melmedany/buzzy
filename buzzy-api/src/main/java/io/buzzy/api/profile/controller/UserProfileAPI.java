package io.buzzy.api.profile.controller;

import io.buzzy.api.profile.controller.model.SearchProfileDTO;
import io.buzzy.api.profile.controller.model.SettingsDTO;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.common.web.model.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "UserProfileAPI")
public interface UserProfileAPI {

    @Operation(method = "get", operationId = "getUserProfile", summary = "Get loggedIn user profile",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class))},
            responses = {@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(schema = @Schema(implementation = APIResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @GetMapping(value = "/users/profiles", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    ResponseEntity<APIResponse<UserProfileDTO>> getUserProfile();

    @Operation(method = "put", operationId = "updateUserProfile", summary = "Updated loggedIn user profile",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class))},
            responses = {@ApiResponse(responseCode = "204", description = "No Content", content = {@Content(schema = @Schema(implementation = APIResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @PutMapping(value = "/users/profiles", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<APIResponse<Void>> updateUserProfile(@RequestBody UserProfileDTO updatedProfile);

    @Operation(method = "get", operationId = "searchUserProfiles", summary = "Search available user profiles by keyword",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.QUERY, name = "keyword", schema = @Schema(implementation = String.class))},
            responses = {@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(schema = @Schema(implementation = APIResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @GetMapping(value = "/users/profiles/search", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    ResponseEntity<APIResponse<List<SearchProfileDTO>>> searchUserProfiles(@RequestParam String keyword);

    @Operation(method = "put", operationId = "addConnection", summary = "Add new connection to loggedIn user profile",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class))},
            responses = {@ApiResponse(responseCode = "201", description = "Created")})
    @PutMapping(value = "/users/profiles/connections/add/{idToConnect}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    ResponseEntity<APIResponse<Void>> addConnection(@PathVariable String idToConnect);

    @Operation(method = "put", operationId = "updateSettings", summary = "Get loggedIn user profile",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = SettingsDTO.class), mediaType = "application/json")),
            responses = {@ApiResponse(responseCode = "201", description = "Created")})
    @PutMapping(value = "/users/profiles/settings", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    ResponseEntity<APIResponse<Void>> updateSettings(@RequestBody SettingsDTO registrationRequest);


}
