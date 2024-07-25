package io.buzzy.websockets.server.messaging.controller;

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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Tag(name = "ConversationMessageAPI")
public interface ConversationMessageAPI {

    @Operation(summary = "Set conversation message to read state",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.PATH, name = "conversationId", schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.PATH, name = "messageId", schema = @Schema(implementation = String.class))},
            responses = {@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(schema = @Schema(implementation = APIResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @MessageMapping("/conversations/{conversationId}/messages/{messageId}/read/")
    ResponseEntity<APIResponse<Void>> setReadState(@DestinationVariable("conversationId") String conversationId,
                                                     @DestinationVariable("messageId") String messageId,
                                                     SimpMessageHeaderAccessor headerAccessor);

    @Operation(summary = "Bulk set conversation message to read state",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.AUTHORIZATION, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.PATH, name = "conversationId", schema = @Schema(implementation = String.class)),
                    @Parameter(in = ParameterIn.PATH, name = "messageId", schema = @Schema(implementation = String.class))},
            responses={@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(schema = @Schema(implementation = APIResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @MessageMapping("/conversations/{conversationId}/messages/{messageId}/bulkRead/")
    ResponseEntity<APIResponse<Void>> bulkSetReadState(@DestinationVariable String conversationId,
                                                         @DestinationVariable("messageId") String messageId,
                                                         SimpMessageHeaderAccessor headerAccessor);
}
