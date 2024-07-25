package io.buzzy.sso.authentication.provider;

import io.buzzy.sso.authentication.ApiAuthenticationProvider;
import io.buzzy.sso.authentication.ApiAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.util.Map;
import java.util.Set;

import static io.buzzy.sso.authentication.ApiAuthenticationToken.GRANT_API;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiAuthenticationProviderTest {

    @Mock
    private OAuth2AuthorizationService authorizationService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ApiAuthenticationProvider authProvider;

    @Test
    public void testAuthenticate_ValidCredentials() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("password123");
        when(passwordEncoder.matches("password123", userDetails.getPassword())).thenReturn(true);

        RegisteredClient registeredClient = mock(RegisteredClient.class);
        when(registeredClient.getAuthorizationGrantTypes()).thenReturn(Set.of(GRANT_API));
        when(registeredClient.getScopes()).thenReturn(Set.of("read","write"));

        OAuth2ClientAuthenticationToken clientPrincipal = mock(OAuth2ClientAuthenticationToken.class);
        when(clientPrincipal.getName()).thenReturn("registeredclientprincipalname");
        when(clientPrincipal.getRegisteredClient()).thenReturn(registeredClient);
        when(clientPrincipal.isAuthenticated()).thenReturn(true);

        AuthorizationServerContext authorizationServerContext = mock(AuthorizationServerContext.class);
        AuthorizationServerContextHolder.setContext(authorizationServerContext);

        SecurityContext mockedSecurityContext = mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(clientPrincipal);
        SecurityContextHolder.setContext(mockedSecurityContext);

        ApiAuthenticationToken authenticationToken = new ApiAuthenticationToken(clientPrincipal, "testuser", "password123", Set.of(), Map.of());
        authenticationToken.setDetails(clientPrincipal);

        OAuth2Token mockedToken = mock(OAuth2Token.class);
        when(mockedToken.getTokenValue()).thenReturn("tokenvalue");

        doReturn(mockedToken).when(tokenGenerator).generate(any(OAuth2TokenContext.class));

        Authentication authResult = authProvider.authenticate(authenticationToken);

        verify(authorizationService, times(1)).save(any());

        assertNotNull(authResult);
        assertInstanceOf(OAuth2AccessTokenAuthenticationToken.class, authResult);
        assertEquals(clientPrincipal, authResult.getPrincipal());
    }

    @Test
    public void testAuthenticate_UnknownRegisteredClient() {
        ApiAuthenticationToken authenticationToken = new ApiAuthenticationToken(mock(Authentication.class), "testuser", "password123", Set.of(), Map.of());
        assertThrows(OAuth2AuthenticationException.class, () -> authProvider.authenticate(authenticationToken));
        verify(authorizationService, never()).save(any());
    }

    @Test
    public void testAuthenticate_NullUserDetails() {
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(null);

        RegisteredClient registeredClient = mock(RegisteredClient.class);
        when(registeredClient.getAuthorizationGrantTypes()).thenReturn(Set.of(GRANT_API));

        OAuth2ClientAuthenticationToken clientPrincipal = mock(OAuth2ClientAuthenticationToken.class);
        when(clientPrincipal.getRegisteredClient()).thenReturn(registeredClient);
        when(clientPrincipal.isAuthenticated()).thenReturn(true);

        ApiAuthenticationToken authenticationToken = new ApiAuthenticationToken(clientPrincipal, "testuser", "password123", Set.of(), Map.of());

        assertThrows(OAuth2AuthenticationException.class, () -> authProvider.authenticate(authenticationToken));

        verify(authorizationService, never()).save(any());
    }

    @Test
    public void testAuthenticate_GeneratedTokenIsNull() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("password123");
        when(passwordEncoder.matches("password123", userDetails.getPassword())).thenReturn(true);

        RegisteredClient registeredClient = mock(RegisteredClient.class);
        when(registeredClient.getAuthorizationGrantTypes()).thenReturn(Set.of(GRANT_API));

        OAuth2ClientAuthenticationToken clientPrincipal = mock(OAuth2ClientAuthenticationToken.class);
        when(clientPrincipal.getRegisteredClient()).thenReturn(registeredClient);
        when(clientPrincipal.isAuthenticated()).thenReturn(true);

        AuthorizationServerContext authorizationServerContext = mock(AuthorizationServerContext.class);
        AuthorizationServerContextHolder.setContext(authorizationServerContext);

        SecurityContext mockedSecurityContext = mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication()).thenReturn(clientPrincipal);
        SecurityContextHolder.setContext(mockedSecurityContext);

        ApiAuthenticationToken authenticationToken = new ApiAuthenticationToken(clientPrincipal, "testuser", "password123", Set.of(), Map.of());
        authenticationToken.setDetails(clientPrincipal);

        when(tokenGenerator.generate(any())).thenReturn(null);

        assertThrows(OAuth2AuthenticationException.class, () -> authProvider.authenticate(authenticationToken));

        verify(authorizationService, never()).save(any());
    }


    @Test
    public void testAuthenticate_NullAuthentication() {
        assertThrows(OAuth2AuthenticationException.class, () -> authProvider.authenticate(null));
        verify(authorizationService, never()).save(any());
    }

    @Test
    public void testAuthenticate_InvalidApiAuthenticationToken() {
        Authentication invalidToken = mock(Authentication.class);

        assertThrows(OAuth2AuthenticationException.class, () -> authProvider.authenticate(invalidToken));
        verify(authorizationService, never()).save(any());
    }

    @Test
    public void testAuthenticate_EmptyUsername() {
        ApiAuthenticationToken authenticationToken = new ApiAuthenticationToken(mock(Authentication.class), "", "password123", Set.of(), Map.of());

        assertThrows(OAuth2AuthenticationException.class, () -> authProvider.authenticate(authenticationToken));
        verify(authorizationService, never()).save(any());
    }

    @Test
    public void testAuthenticate_EmptyPassword() {
        ApiAuthenticationToken authenticationToken = new ApiAuthenticationToken(mock(Authentication.class), "testuser", "", Set.of(), Map.of());

        assertThrows(OAuth2AuthenticationException.class, () -> authProvider.authenticate(authenticationToken));
        verify(authorizationService, never()).save(any());
    }

}
