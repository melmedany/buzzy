SET TIME ZONE 'UTC';

CREATE TABLE IF NOT EXISTS users_profiles (
      id             UUID                                             PRIMARY KEY,
      user_id        UUID                                             UNIQUE NOT NULL,
      username       VARCHAR(50)                                      UNIQUE NOT NULL,
      firstname      VARCHAR(100),
      lastname       VARCHAR(100),
      active         BOOLEAN           DEFAULT TRUE                   NOT NULL ,
      settings       JSONB             DEFAULT NULL,
      last_seen      TIMESTAMP         DEFAULT NULL,
      created        TIMESTAMP                                        NOT NULL,
      updated        TIMESTAMP         DEFAULT CURRENT_TIMESTAMP      NOT NULL
);

CREATE TABLE users_connections (
      user_id            UUID                                         NOT NULL,
      connection_id      UUID                                         NOT NULL,
      PRIMARY KEY (user_id, connection_id),
      FOREIGN KEY (user_id) REFERENCES users_profiles(id),
      FOREIGN KEY (connection_id) REFERENCES users_profiles(id)
);