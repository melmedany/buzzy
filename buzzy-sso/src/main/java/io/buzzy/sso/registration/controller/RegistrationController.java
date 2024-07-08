package io.buzzy.sso.registration.controller;

import io.buzzy.common.web.model.APIResponse;
import io.buzzy.sso.registration.controller.model.SignupRequest;
import io.buzzy.sso.registration.controller.model.UserDTO;
import io.buzzy.sso.registration.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class RegistrationController implements RegistrationAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public ResponseEntity<APIResponse<UserDTO>> signup(SignupRequest signupRequest) {
        LOGGER.debug("Creating new user account");
        UserDTO userDTO = registrationService.createUser(signupRequest);
        return new ResponseEntity<>(new APIResponse<>(userDTO, null), HttpStatus.OK);
    }

}
