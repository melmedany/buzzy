SET TIME ZONE 'UTC';

CREATE TABLE IF NOT EXISTS users_profiles (
      id             UUID                                             PRIMARY KEY,
      user_id        UUID                                             UNIQUE NOT NULL,
      username       VARCHAR(50)                                      UNIQUE NOT NULL,
      firstname      VARCHAR(100),
      lastname       VARCHAR(100),
      active         BOOLEAN           DEFAULT TRUE                   NOT NULL ,
      settings       JSONB             DEFAULT NULL,
      last_seen        TIMESTAMP         DEFAULT NULL,
      created        TIMESTAMP                                        NOT NULL,
      updated        TIMESTAMP         DEFAULT CURRENT_TIMESTAMP      NOT NULL
);

INSERT INTO users_profiles(id, user_id, username, firstname, lastname, active, settings, created) VALUES
         ('2c1d3268-0643-48d9-b6d3-5f8edaec4559', 'd4ba3822-e725-4732-978c-25031fabc61f', 'mohamed', 'Mohamed', 'Elmedany', TRUE, '{}', CURRENT_TIMESTAMP);


CREATE TABLE users_connections (
      user_id            UUID                                         NOT NULL,
      connection_id      UUID                                         NOT NULL,
      PRIMARY KEY (user_id, connection_id),
      FOREIGN KEY (user_id) REFERENCES users_profiles(id),
      FOREIGN KEY (connection_id) REFERENCES users_profiles(id)
);