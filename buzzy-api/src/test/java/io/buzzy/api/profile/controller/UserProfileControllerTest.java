package io.buzzy.api.profile.controller;

import io.buzzy.api.profile.controller.model.SearchProfileDTO;
import io.buzzy.api.profile.controller.model.SettingsDTO;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.service.UserProfileService;
import io.buzzy.common.service.ResourceBundleMessagesService;
import io.buzzy.common.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private ResourceBundleMessagesService bundleMessagesService;

    @Test
    void testGetUserProfile() throws Exception {
        UserProfileDTO mockProfile = new UserProfileDTO();
        mockProfile.setUsername("testUser");
        when(userProfileService.findByUsername("testUser")).thenReturn(mockProfile);

        mockMvc.perform(get("/v1/users/profiles")
                        .with(user("testUser").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testSearchUserProfiles() throws Exception {
        List<SearchProfileDTO> mockProfiles = new ArrayList<>();
        when(userProfileService.searchUserProfiles("keyword", "user1")).thenReturn(mockProfiles);

        mockMvc.perform(get("/v1/users/profiles/search")
                        .param("keyword", "keyword")
                        .with(user("testUser").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testAddConnection() throws Exception {
        mockMvc.perform(put("/v1/users/profiles/connections/add/123" )
                        .with(user("testUser").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateSettings() throws Exception {
        mockMvc.perform(put("/v1/users/profiles/settings")
                        .with(user("testUser").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new SettingsDTO())))
                .andExpect(status().isCreated());
    }
}
