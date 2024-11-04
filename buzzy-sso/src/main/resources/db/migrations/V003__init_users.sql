SET TIME ZONE 'UTC';

CREATE TABLE IF NOT EXISTS privileges (
   id             UUID                    PRIMARY KEY,
   name           VARCHAR(50)             UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
    id             UUID                    PRIMARY KEY,
    name           VARCHAR(50)             UNIQUE NOT NULL
);


CREATE TABLE IF NOT EXISTS users (
    id             UUID                                             PRIMARY KEY,
    username       VARCHAR(50)                                      UNIQUE NOT NULL,
    password       BYTEA                                            NOT NULL,
    firstname      VARCHAR(100),
    lastname       VARCHAR(100),
    active         BOOLEAN           DEFAULT TRUE                   NOT NULL ,
    created        TIMESTAMP                                        NOT NULL,
    updated        TIMESTAMP         DEFAULT CURRENT_TIMESTAMP      NOT NULL
);

CREATE TABLE IF NOT EXISTS roles_privileges (
    privilege_id        UUID,
    role_id             UUID,
    CONSTRAINT unique_role_privilege UNIQUE (privilege_id, role_id),
    foreign key (role_id) references roles(id),
    foreign key (privilege_id) references privileges(id)
);

CREATE TABLE IF NOT EXISTS users_roles (
    user_id        UUID,
    role_id        UUID,
    CONSTRAINT unique_user_role UNIQUE (user_id, role_id),
    foreign key (user_id) references users(id),
    foreign key (role_id) references roles(id)
);


INSERT INTO roles (id, name) VALUES ('ba24b046-edd2-4727-8f21-131ac25fca9a', 'USER');
INSERT INTO roles (id, name) VALUES ('56a983a8-15f0-46dd-9f54-d9accbae9177', 'ADMIN');

INSERT INTO users (id, username, password, firstname, lastname, active, created) VALUES ('c2d29867-3d0b-d497-9191-18a9d8ee7830', 'admin', '{bcrypt}$2a$10$Ev8SmWKTQdnFdVuKwpv9VuBA66NFLk6C8Iu5PoZk1eYV6XdfObPx2', 'SYSTEM', '', TRUE, CURRENT_TIMESTAMP);

INSERT INTO users_roles (user_id, role_id) VALUES ('c2d29867-3d0b-d497-9191-18a9d8ee7830', '56a983a8-15f0-46dd-9f54-d9accbae9177');
