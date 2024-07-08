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
    firstname       VARCHAR(100),
    lastname        VARCHAR(100),
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

-- INSERT INTO privileges (id, name) VALUES ('2ff81e2d-465d-4032-a1de-4c7f0cfa3097', 'NONE');
-- INSERT INTO privileges (id, name) VALUES ('26a6eed4-096c-4094-9fb9-8082c82f5d1d', 'ALL');

INSERT INTO roles (id, name) VALUES ('ba24b046-edd2-4727-8f21-131ac25fca9a', 'USER');
INSERT INTO roles (id, name) VALUES ('56a983a8-15f0-46dd-9f54-d9accbae9177', 'ADMIN');

INSERT INTO users (id, username, password, firstname, lastname, active, created) VALUES ('c2d29867-3d0b-d497-9191-18a9d8ee7830', 'admin', '{bcrypt}$2a$10$Ev8SmWKTQdnFdVuKwpv9VuBA66NFLk6C8Iu5PoZk1eYV6XdfObPx2', 'SYSTEM', '', TRUE, CURRENT_TIMESTAMP);
INSERT INTO users (id, username, password, firstname, lastname, active, created) VALUES ('2c1d3268-0643-48d9-b6d3-5f8edaec4559', 'mohamed', '{bcrypt}$2a$10$JTRLmQGS6.XoFzWPUU/Q1Ol60QaMoYuibV2zra5tNJj2XoKgrgVfm', 'Mohamed', 'Elmedany', TRUE, CURRENT_TIMESTAMP);

-- INSERT INTO roles_privileges (role_id, privilege_id) VALUES ('ba24b046-edd2-4727-8f21-131ac25fca9a', '2ff81e2d-465d-4032-a1de-4c7f0cfa3097');
-- INSERT INTO roles_privileges (role_id, privilege_id) VALUES ('56a983a8-15f0-46dd-9f54-d9accbae9177', '26a6eed4-096c-4094-9fb9-8082c82f5d1d');

INSERT INTO users_roles (user_id, role_id) VALUES ('c2d29867-3d0b-d497-9191-18a9d8ee7830', '56a983a8-15f0-46dd-9f54-d9accbae9177');
INSERT INTO users_roles (user_id, role_id) VALUES ('2c1d3268-0643-48d9-b6d3-5f8edaec4559', 'ba24b046-edd2-4727-8f21-131ac25fca9a');