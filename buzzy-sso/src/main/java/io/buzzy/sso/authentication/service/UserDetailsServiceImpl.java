package io.buzzy.sso.authentication.service;

import io.buzzy.sso.core.repository.UserRepository;
import io.buzzy.sso.core.repository.entity.Privilege;
import io.buzzy.sso.core.repository.entity.Role;
import io.buzzy.sso.core.repository.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> dbUser = userRepository.findByUsernameAndActive(username, true);

        return dbUser.map(u -> org.springframework.security.core.userdetails.User.builder()
                .username(u.getUsername())
                .password(new String(u.getPassword()))
                .authorities(getAuthorities(u.getRoles()))
                .build()).orElseThrow(() -> new UsernameNotFoundException("global.user.not.found"));
    }


    private String[] getAuthorities(Collection<Role> roles) {
        return Stream.concat(roles.stream().map(Role::getName),
                roles.stream().map(Role::getPrivileges)
                        .flatMap(Collection::stream)
                        .map(Privilege::getName))
//                .map(roleName -> "ROLE_" + roleName)
                .toArray(String[]::new);
    }
}
