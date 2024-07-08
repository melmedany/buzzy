package io.buzzy.api.profile.controller;


import io.buzzy.api.profile.controller.model.SettingsDTO;
import io.buzzy.api.profile.controller.model.UserProfileDTO;
import io.buzzy.api.profile.service.UserProfileService;
import io.buzzy.common.web.model.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class UserProfileController implements UserProfileAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public ResponseEntity<APIResponse<UserProfileDTO>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();

        UserProfileDTO profile = userProfileService.findByUsername(principalName);

        return new ResponseEntity<>(new APIResponse<>(profile, null), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIResponse<List<UserProfileDTO>>> searchUserProfiles(String keyword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();

        List<UserProfileDTO> profiles = userProfileService.searchUserProfiles(keyword, principalName);

        return new ResponseEntity<>(new APIResponse<>(profiles, null), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIResponse<Void>> addConnection(String userProfileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();

        userProfileService.addConnection(principalName, userProfileId);

        return new ResponseEntity<>(APIResponse.emptyResponse(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<APIResponse<Void>> updateSettings(SettingsDTO settingsDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();

        userProfileService.updateSettings(principalName, settingsDTO);

        return new ResponseEntity<>(APIResponse.emptyResponse(), HttpStatus.CREATED);
    }
}
