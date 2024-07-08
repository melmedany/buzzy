SET TIME ZONE 'UTC';

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




INSERT INTO oauth2_registered_client VALUES (
     1,
     'buzzy-api',
     CURRENT_TIMESTAMP,
     '{bcrypt}$2a$10$sBdUqgh/yboCMaeJJBP7UOpThEIm19tbBIaAvmwB5XCYDwxLd4vWe',
     null,
     'Buzzy Conversation API',
     'client_secret_basic', -- missing post
     'refresh_token,grant_api',
     'http://buzzy.io/login/oauth2/code/buzzy-api',
     null,
     'read,write',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS512"],"settings.token.access-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.authorization-code-time-to-live":["java.time.Duration","PT10M"],"settings.token.device-code-time-to-live":["java.time.Duration","PT5M"]}'),
    (2,
     'buzzy-webapp',
     CURRENT_TIMESTAMP,
     '{bcrypt}$2a$10$4ZuEg.OMhjwq8hXepJBbSu9yYUjiYEYKQvFxMNaumkWA2d0L7pCMy',
     null,
     'Buzzy Web App',
     'client_secret_basic', -- missing post
     'refresh_token,grant_api',
     'http://buzzy.io/login/oauth2/code/buzzy-webapp',
     null,
     'read,write',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS512"],"settings.token.access-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.authorization-code-time-to-live":["java.time.Duration","PT10M"],"settings.token.device-code-time-to-live":["java.time.Duration","PT5M"]}'),
    (3,
     'buzzy-websockets-server',
     CURRENT_TIMESTAMP,
     '{bcrypt}$2a$10$bPC./7LjBTMEpXJoPpaBa.TSI3Jgz86vckklJbbt9XATJHMSqzciW',
     null,
     'Buzzy WebSockets server',
     'client_secret_basic', -- missing post
     'grant_api',
     'http://buzzy.io/login/oauth2/code/buzzy-websockets-server',
     null,
     'read,write',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS512"],"settings.token.access-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration","PT5H"],"settings.token.authorization-code-time-to-live":["java.time.Duration","PT10M"],"settings.token.device-code-time-to-live":["java.time.Duration","PT5M"]}')