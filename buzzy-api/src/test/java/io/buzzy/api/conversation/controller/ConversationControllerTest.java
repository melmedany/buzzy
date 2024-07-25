package io.buzzy.api.conversation.controller;

import io.buzzy.api.conversation.controller.model.ConversationDTO;
import io.buzzy.api.conversation.controller.model.PostMessageRequest;
import io.buzzy.api.conversation.service.ConversationMessageService;
import io.buzzy.api.conversation.service.ConversationService;
import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.common.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private ConversationMessageService conversationMessageService;

    @MockBean
    private ResourceBundleMessagesService bundleMessagesService;

    @Test
    void testGetConversationsSummary() throws Exception {
        ConversationDTO conversationDTO = new ConversationDTO();
        conversationDTO.setId(UUID.randomUUID().toString());
        List<ConversationDTO> summary = List.of(conversationDTO);
        when(conversationService.getConversationsSummary(anyString())).thenReturn(summary);

        mockMvc.perform(get("/v1/conversations/summary")
                        .with(user("testUser").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").isNotEmpty());
    }

    @Test
    void testGetConversation() throws Exception {
        ConversationDTO conversationDTO = new ConversationDTO();
        conversationDTO.setId(UUID.randomUUID().toString());
        when(conversationService.getConversation(anyString(), anyString())).thenReturn(conversationDTO);

        mockMvc.perform(get("/v1/conversations/{id}", "1")
                        .with(user("testUser").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void testPostMessage() throws Exception {
        PostMessageRequest request = new PostMessageRequest();
        request.setMessage("Hello");

        mockMvc.perform(post("/v1/conversations/{conversationId}/messages", "1")
                        .with(user("testUser").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf())
                        .contentType("application/json")
                        .content(JsonUtil.toJson(request)))
                .andExpect(status().isCreated());
    }
}