SET TIME ZONE 'UTC';

CREATE TABLE IF NOT EXISTS conversations (
    id             UUID                                             PRIMARY KEY,
    type           VARCHAR(20)                                      NOT NULL,
    created        TIMESTAMP                                        NOT NULL,
    updated        TIMESTAMP         DEFAULT CURRENT_TIMESTAMP      NOT NULL
);

INSERT INTO conversations (id, type, created) VALUES ('4e87d6e6-c456-4cae-b0e2-82dfeeb03beb', 'direct_message', CURRENT_TIMESTAMP);


CREATE TABLE IF NOT EXISTS conversation_messages (
    id                      UUID                                             PRIMARY KEY,
    type                    VARCHAR(20)                                      NOT NULL,
    state                   VARCHAR(20)                                      NOT NULL,
    conversation_id         UUID                                             NOT NULL,
    sender                  UUID                                             NOT NULL,
    text                    TEXT,
    created                 TIMESTAMP                                        NOT NULL,
    updated                 TIMESTAMP         DEFAULT CURRENT_TIMESTAMP      NOT NULL,
    foreign key (conversation_id) references conversations(id),
    foreign key (sender) references users_profiles(id)
);

INSERT INTO conversation_messages (id, type, state, conversation_id, sender, text, created) VALUES
        ('eb34b770-5108-4a6f-a73a-7e292dfc42d6', 'text', 'sent', '4e87d6e6-c456-4cae-b0e2-82dfeeb03beb', '2c1d3268-0643-48d9-b6d3-5f8edaec4559', 'First message!', CURRENT_TIMESTAMP - INTERVAL '3 hour' ),
        ('930ff1ad-6405-4544-a2ce-9ccc8e20bbd7', 'text', 'delivered', '4e87d6e6-c456-4cae-b0e2-82dfeeb03beb', '2c1d3268-0643-48d9-b6d3-5f8edaec4559', 'Second message!', CURRENT_TIMESTAMP - INTERVAL '2 hour'),
        ('6867ab01-c4bc-455a-9cb5-7bf689eb1055', 'text', 'read', '4e87d6e6-c456-4cae-b0e2-82dfeeb03beb', '2c1d3268-0643-48d9-b6d3-5f8edaec4559', 'Third message!', CURRENT_TIMESTAMP - INTERVAL '1 hour');

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

INSERT INTO users_conversations (user_id, conversation_id) VALUES
    ('2c1d3268-0643-48d9-b6d3-5f8edaec4559', '4e87d6e6-c456-4cae-b0e2-82dfeeb03beb');



