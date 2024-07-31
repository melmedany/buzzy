package io.buzzy.sso.registration.service;

import io.buzzy.sso.core.repository.RoleRepository;
import io.buzzy.sso.core.repository.RolesEnum;
import io.buzzy.sso.core.repository.UserRepository;
import io.buzzy.sso.core.repository.entity.Role;
import io.buzzy.sso.core.repository.entity.User;
import io.buzzy.sso.registration.controller.model.SignupRequest;
import io.buzzy.sso.registration.mapper.UserMapper;
import io.buzzy.sso.registration.service.exception.UsernameAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;

@Service
public class RegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class);
    private static final Set<String> DEFAULT_USER_ROLES = Set.of(RolesEnum.USER.name());

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SuccessfulRegistrationProducer successfulRegistrationProducer;
    private final UserMapper userMapper;

    public RegistrationService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository,
                               SuccessfulRegistrationProducer successfulRegistrationProducer, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.successfulRegistrationProducer = successfulRegistrationProducer;
        this.userMapper = userMapper;
    }

    public void createUser(SignupRequest signupRequest) {
        if (usernameExists(signupRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("validation.username.already.exists");
        }

        User user = userMapper.toUser(signupRequest);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()).getBytes(StandardCharsets.UTF_8));
        user.setActive(true);
        applyDefaultRoles(user);

        user = userRepository.save(user);

        if (user.getId() != null) {
            successfulRegistrationProducer.send(userMapper.toSuccessfulRegistrationDTO(user));
        }
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private void applyDefaultRoles(User user) {
        Collection<Role> defaultRoles = roleRepository.findByNameIn(DEFAULT_USER_ROLES);
        user.setRoles(defaultRoles);
    }

}
