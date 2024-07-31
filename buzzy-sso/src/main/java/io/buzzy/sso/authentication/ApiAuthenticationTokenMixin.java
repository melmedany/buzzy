package io.buzzy.sso.authentication;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

import java.io.IOException;
import java.util.HashMap;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = ApiAuthenticationTokenDeserializer.class)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ApiAuthenticationTokenMixin {
}

class ApiAuthenticationTokenDeserializer extends JsonDeserializer<OAuth2ClientAuthenticationToken> {

    @Override
    public OAuth2ClientAuthenticationToken deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode root = mapper.readTree(parser);


        String PRINCIPAL_KEY = "principal";
        String CLIENT_AUTHENTICATION_METHOD_KEY = "clientAuthenticationMethod";
        String CLIENT_AUTHENTICATION_METHOD_VALUE_KEY = "value";
        String CREDENTIALS_KEY = "credentials";
        OAuth2ClientAuthenticationToken token = new OAuth2ClientAuthenticationToken(root.get(PRINCIPAL_KEY).asText(),
                new ClientAuthenticationMethod(root.get(CLIENT_AUTHENTICATION_METHOD_KEY)
                        .get(CLIENT_AUTHENTICATION_METHOD_VALUE_KEY).asText()),
                root.get(CREDENTIALS_KEY).asText(), new HashMap<>());
        String DETAILS_KEY = "details";
        token.setDetails(mapper.convertValue(root.get(DETAILS_KEY), User.class));

        return token;
    }
}