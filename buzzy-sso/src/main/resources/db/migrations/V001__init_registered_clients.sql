CREATE TABLE oauth2_registered_client (
                                          id varchar(100) NOT NULL,
                                          client_id varchar(100) NOT NULL,
                                          client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                          client_secret varchar(200) DEFAULT NULL,
                                          client_secret_expires_at timestamp DEFAULT NULL,
                                          client_name varchar(200) NOT NULL,
                                          client_authentication_methods varchar(1000) NOT NULL,
                                          authorization_grant_types varchar(1000) NOT NULL,
                                          redirect_uris varchar(1000) DEFAULT NULL,
                                          post_logout_redirect_uris varchar(1000) DEFAULT NULL,
                                          scopes varchar(1000) NOT NULL,
                                          client_settings varchar(2000) NOT NULL,
                                          token_settings varchar(2000) NOT NULL,
                                          PRIMARY KEY (id)
);




INSERT INTO oauth2_registered_client VALUES (1,
                                             'buzzy-conversation-api',
                                             current_timestamp,
                                             '{bcrypt}$2a$10$rtt/WUjZBLlQZMSeoOZbSeDGEPAaURRRnpLz.nNeqQM9nktK7X1Wu',
                                             null,
                                             'Buzzy Conversation API',
                                             'client_secret_basic', -- missing post
                                             'refresh_token,grant_api',
                                             'http://buzzy.io/login/oauth2/code/buzzy-conversation-api',
                                             null,
                                             'read,write',
                                             '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
--                                              '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}'
                                             '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS512"],"settings.token.access-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.authorization-code-time-to-live":["java.time.Duration","PT10M"],"settings.token.device-code-time-to-live":["java.time.Duration","PT5M"]}'
                                            ),
                                            (2,
                                             'buzzy-webapp',
                                             current_timestamp,
                                             '{bcrypt}$2a$10$4ZuEg.OMhjwq8hXepJBbSu9yYUjiYEYKQvFxMNaumkWA2d0L7pCMy',
                                             null,
                                             'Buzzy Web App',
                                             'client_secret_basic', -- missing post
                                             'refresh_token,grant_api',
                                             'http://buzzy.io/login/oauth2/code/buzzy-webapp',
                                             null,
                                             'read,write',
                                             '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
--                                              '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}'
                                             '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS512"],"settings.token.access-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.authorization-code-time-to-live":["java.time.Duration","PT10M"],"settings.token.device-code-time-to-live":["java.time.Duration","PT5M"]}'
                                            );



-- CREATE TABLE IF NOT EXISTS buzzy_client_authorizations (
--     id                                  UUID                            PRIMARY KEY,
--     registeredClientId                  UUID                            NOT NULL,
--     principalName                       VARCHAR(255)                    NOT NULL,
--     authorizationGrantType              VARCHAR(255)                    NOT NULL,
--     authorizedScopes                    VARCHAR(1000)                   DEFAULT NULL,
--     attributes                          VARCHAR(4000)                   DEFAULT NULL,
--     state                               VARCHAR(500)                    DEFAULT NULL,
--     authorizationCodeValue              VARCHAR(4000)                   DEFAULT NULL,
--     authorizationCodeIssuedAt           TIMESTAMP                       DEFAULT NULL,
--     authorizationCodeExpiresAt          TIMESTAMP                       DEFAULT NULL,
--     authorizationCodeMetadata           VARCHAR(2000)                   DEFAULT NULL,
--     accessTokenValue                    VARCHAR(4000)                   DEFAULT NULL,
--     accessTokenIssuedAt                 TIMESTAMP                       DEFAULT NULL,
--     accessTokenExpiresAt                TIMESTAMP                       DEFAULT NULL,
--     accessTokenMetadata                 VARCHAR(2000)                   DEFAULT NULL,
--     accessTokenType                     VARCHAR(255)                    DEFAULT NULL,
--     accessTokenScopes                   VARCHAR(1000)                   DEFAULT NULL,
--     refreshTokenValue                   VARCHAR(4000)                   DEFAULT NULL,
--     refreshTokenIssuedAt                TIMESTAMP                       DEFAULT NULL,
--     refreshTokenExpiresAt               TIMESTAMP                       DEFAULT NULL,
--     refreshTokenMetadata                VARCHAR(2000)                   DEFAULT NULL,
--     oidcIdTokenValue                    VARCHAR(4000)                   DEFAULT NULL,
--     oidcIdTokenIssuedAt                 TIMESTAMP                       DEFAULT NULL,
--     oidcIdTokenExpiresAt                TIMESTAMP                       DEFAULT NULL,
--     oidcIdTokenMetadata                 VARCHAR(2000)                   DEFAULT NULL,
--     oidcIdTokenClaims                   VARCHAR(2000)                   DEFAULT NULL,
--     userCodeValue                       VARCHAR(4000)                   DEFAULT NULL,
--     userCodeIssuedAt                    TIMESTAMP                       DEFAULT NULL,
--     userCodeExpiresAt                   TIMESTAMP                       DEFAULT NULL,
--     userCodeMetadata                    VARCHAR(2000)                   DEFAULT NULL,
--     deviceCodeValue                     VARCHAR(4000)                   DEFAULT NULL,
--     deviceCodeIssuedAt                  TIMESTAMP                       DEFAULT NULL,
--     deviceCodeExpiresAt                 TIMESTAMP                       DEFAULT NULL,
--     deviceCodeMetadata                  VARCHAR(2000)                   DEFAULT NULL
-- );
--
-- CREATE TABLE IF NOT EXISTS buzzy_client_authorization_consents (
--     registeredClientId                  UUID(255)                       NOT NULL,
--     principalName                       VARCHAR(255)                    NOT NULL,
--     authorities                         VARCHAR(1000)                   NOT NULL,
--     PRIMARY KEY (registeredClientId, principalName)
-- );


