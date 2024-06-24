package io.buzzy.sso.registration.service;

import io.buzzy.sso.registration.controller.model.SignupRequest;
import io.buzzy.sso.registration.controller.model.UserDTO;
import io.buzzy.sso.core.repository.entity.User;
import io.buzzy.sso.core.repository.UserRepository;
import io.buzzy.sso.registration.service.exception.UsernameAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class RegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public RegistrationService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

    }

    public UserDTO createUser(SignupRequest signupRequest) {
        if (usernameExists(signupRequest.username())) {
            throw new UsernameAlreadyExistsException("validation.username.already.exists");
        }

        User user = new User();

        user.setUsername(signupRequest.username());
        user.setFirstname(signupRequest.firstname());
        user.setLastname(signupRequest.lastname());
        user.setPassword(passwordEncoder.encode(signupRequest.password()).getBytes(StandardCharsets.UTF_8));
        user.setActive(true);

        userRepository.save(user);
        return userDTO(user);
    }

    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    private UserDTO userDTO(User user) {
        return new UserDTO(user.getId().toString(), user.getUsername(), user.getFirstname(), user.getLastname());
    }



    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
