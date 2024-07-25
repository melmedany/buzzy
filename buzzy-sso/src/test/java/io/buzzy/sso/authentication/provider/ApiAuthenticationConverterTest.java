package io.buzzy.sso.authentication.provider;

import io.buzzy.sso.authentication.ApiAuthenticationConverter;
import io.buzzy.sso.authentication.ApiAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiAuthenticationConverterTest {

    @InjectMocks
    private ApiAuthenticationConverter apiAuthenticationConverter;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("testuser", null));
    }


    @Test
    public void testConvert_Success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn(ApiAuthenticationToken.GRANT_API.getValue());
        when(request.getParameterMap()).thenReturn(Map.of(
                OAuth2ParameterNames.GRANT_TYPE, new String[]{ApiAuthenticationToken.GRANT_API.getValue()},
                OAuth2ParameterNames.SCOPE, new String[]{"read"},
                OAuth2ParameterNames.USERNAME, new String[]{"testuser"},
                OAuth2ParameterNames.PASSWORD, new String[]{"password"}
        ));

        Authentication result = apiAuthenticationConverter.convert(request);

        assertNotNull(result);
        assertInstanceOf(ApiAuthenticationToken.class, result);
    }

    @Test
    public void testConvert_InvalidGrantType() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn("password");

        Authentication result = apiAuthenticationConverter.convert(request);

        assertNull(result);
    }

    @Test
    public void testConvert_InvalidScopeParameter() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn(ApiAuthenticationToken.GRANT_API.getValue());
        when(request.getParameterMap()).thenReturn(Map.of(
                OAuth2ParameterNames.SCOPE, new String[]{"read","write"},
                OAuth2ParameterNames.USERNAME, new String[]{"testuser"},
                OAuth2ParameterNames.PASSWORD, new String[]{"password"}
        ));

        assertThrows(OAuth2AuthenticationException.class, () -> apiAuthenticationConverter.convert(request));
    }

    @Test
    public void testConvert_MissingUsernameParameter() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn(ApiAuthenticationToken.GRANT_API.getValue());
        when(request.getParameterMap()).thenReturn(Map.of(
                OAuth2ParameterNames.GRANT_TYPE, new String[]{ApiAuthenticationToken.GRANT_API.getValue()},
                OAuth2ParameterNames.SCOPE, new String[]{"read"},
                OAuth2ParameterNames.PASSWORD, new String[]{"password"}
        ));

        assertThrows(OAuth2AuthenticationException.class, () -> apiAuthenticationConverter.convert(request));
    }

    @Test
    public void testConvert_MissingPasswordParameter() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn(ApiAuthenticationToken.GRANT_API.getValue());
        when(request.getParameterMap()).thenReturn(Map.of(
                OAuth2ParameterNames.GRANT_TYPE, new String[]{ApiAuthenticationToken.GRANT_API.getValue()},
                OAuth2ParameterNames.SCOPE, new String[]{"read"},
                OAuth2ParameterNames.USERNAME, new String[]{"testuser"}
        ));

        assertThrows(OAuth2AuthenticationException.class, () -> apiAuthenticationConverter.convert(request));
    }

    @Test
    public void testConvert_AdditionalParameters() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter(OAuth2ParameterNames.GRANT_TYPE)).thenReturn(ApiAuthenticationToken.GRANT_API.getValue());
        when(request.getParameterMap()).thenReturn(Map.of(
                OAuth2ParameterNames.GRANT_TYPE, new String[]{ApiAuthenticationToken.GRANT_API.getValue()},
                OAuth2ParameterNames.SCOPE, new String[]{"read"},
                OAuth2ParameterNames.USERNAME, new String[]{"testuser"},
                OAuth2ParameterNames.PASSWORD, new String[]{"password"},
                "client_id", new String[]{"client_secret"}
        ));

        Authentication result = apiAuthenticationConverter.convert(request);
        assertNotNull(result);
        assertInstanceOf(ApiAuthenticationToken.class, result);
        ApiAuthenticationToken authToken = (ApiAuthenticationToken) result;
        assertEquals("client_secret", authToken.getAdditionalParameters().get("client_id"));
    }
}