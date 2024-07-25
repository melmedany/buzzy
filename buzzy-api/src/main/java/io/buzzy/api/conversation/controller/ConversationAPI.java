package io.buzzy.api.conversation.controller;

import io.buzzy.api.conversation.controller.model.ConversationDTO;
import io.buzzy.api.conversation.controller.model.ConversationMessageDTO;
import io.buzzy.api.conversation.controller.model.PostMessageRequest;
import io.buzzy.common.web.model.APIResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "ConversationAPI")
public interface ConversationAPI {

    @Operation(method = "get", operationId = "getConversationsSummary", summary = "Get conversations summary for authenticated user",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class))},
            responses = {@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(schema = @Schema(implementation = APIResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @GetMapping(value = "/conversations/summary", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    ResponseEntity<APIResponse<List<ConversationDTO>>> getConversationsSummary();

    @Operation(method = "get", operationId = "getConversation", summary = "Get conversation for authenticated user by id",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(implementation = String.class))},
            responses = {@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(schema = @Schema(implementation = APIResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @GetMapping(value = "/conversations/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    ResponseEntity<APIResponse<ConversationDTO>> getConversation(@PathVariable String id);

    @Operation(method = "post", operationId = "postMessage", summary = "Post a message to a conversation",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.PATH, name = "conversationId", schema = @Schema(implementation = String.class))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = PostMessageRequest.class), mediaType = "application/json")),
            responses = {@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping(value = "/conversations/{conversationId}/messages", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    ResponseEntity<APIResponse<ConversationDTO>> postMessage(@PathVariable String conversationId, @Valid @RequestBody PostMessageRequest postMessageRequest);

    @Operation(method = "get", operationId = "getConversationMessage", summary = "Get conversation message for authenticated user by message and conversation IDs",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.PATH, name = "messageId", schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.PATH, name = "conversationId", schema = @Schema(implementation = String.class))},
            responses = {@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(schema = @Schema(implementation = APIResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @GetMapping(value = "/conversations/{conversationId}/messages/{messageId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    ResponseEntity<APIResponse<ConversationMessageDTO>> getConversationMessage(@PathVariable String messageId, @PathVariable String conversationId);
}
