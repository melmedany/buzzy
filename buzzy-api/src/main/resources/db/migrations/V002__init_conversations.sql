SET TIME ZONE 'UTC';

CREATE TABLE IF NOT EXISTS conversations (
    id             UUID                                             PRIMARY KEY,
    type           VARCHAR(20)                                      NOT NULL,
    created        TIMESTAMP                                        NOT NULL,
    updated        TIMESTAMP         DEFAULT CURRENT_TIMESTAMP      NOT NULL
);

CREATE TABLE IF NOT EXISTS conversation_messages (
    id                      UUID                                             PRIMARY KEY,
    type                    VARCHAR(20)                                      NOT NULL,
    state                   VARCHAR(20)                                      NOT NULL,
    conversation_id         UUID                                             NOT NULL,
    sender_id               UUID                                             NOT NULL,
    text                    TEXT,
    created                 TIMESTAMP                                        NOT NULL,
    updated                 TIMESTAMP         DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    foreign key (conversation_id) references conversations(id),
    foreign key (sender_id) references users_profiles(id)
);

CREATE TABLE IF NOT EXISTS conversation_configuration (
     id                      UUID                                             PRIMARY KEY,
     name                    VARCHAR(100),
     conversation_id         UUID                                             NOT NULL,
     created                 TIMESTAMP                                        NOT NULL,
     updated                 TIMESTAMP         DEFAULT CURRENT_TIMESTAMP      NOT NULL,
     foreign key (conversation_id) references conversations(id)
);

CREATE TABLE IF NOT EXISTS users_conversations (
    user_id                     UUID,
    conversation_id             UUID,
    foreign key (user_id) references users_profiles(id),
    foreign key (conversation_id) references conversations(id)
);
