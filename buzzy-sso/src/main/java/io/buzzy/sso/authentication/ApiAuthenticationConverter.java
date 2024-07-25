package io.buzzy.sso.authentication;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static io.buzzy.sso.authentication.ApiAuthenticationToken.GRANT_API;

public class ApiAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);

        if (!GRANT_API.getValue().equals(grantType)) {
            return null;
        }

        MultiValueMap<String, String> parameters = requestParameters(request);

        validateSingleValueParameter(parameters, OAuth2ParameterNames.SCOPE, false);
        validateSingleValueParameter(parameters, OAuth2ParameterNames.USERNAME, true);
        validateSingleValueParameter(parameters, OAuth2ParameterNames.PASSWORD, true);

        Set<String> requestedScopes = StringUtils.hasText(parameters.getFirst(OAuth2ParameterNames.SCOPE)) ?
                new HashSet<>(List.of(StringUtils.delimitedListToStringArray(parameters.getFirst(OAuth2ParameterNames.SCOPE), " "))) : null;

        Map<String, Object> additionalParameters = parameters.entrySet().stream()
                .filter(entry -> !OAuth2ParameterNames.GRANT_TYPE.equals(entry.getKey())
                        && !OAuth2ParameterNames.SCOPE.equals(entry.getKey())
                        && !OAuth2ParameterNames.PASSWORD.equals(entry.getKey())
                        && !OAuth2ParameterNames.USERNAME.equals(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFirst()));

        Authentication principal = SecurityContextHolder.getContext().getAuthentication();

        return new ApiAuthenticationToken(principal, parameters.getFirst(OAuth2ParameterNames.USERNAME),
                parameters.getFirst(OAuth2ParameterNames.PASSWORD), requestedScopes, additionalParameters);
    }

    private MultiValueMap<String, String> requestParameters(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        request.getParameterMap().forEach((key, values) -> parameters.put(key, List.of(values)));
        return parameters;
    }

    private void validateSingleValueParameter(MultiValueMap<String, String> parameters, String paramName, boolean required) {
        if (required && (!StringUtils.hasText(parameters.getFirst(paramName)) || parameters.get(paramName).size() != 1)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST); // TODO // detailed message
        } else if (StringUtils.hasText(parameters.getFirst(paramName)) && parameters.get(paramName).size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST); // TODO // detailed message
        }
    }
}
