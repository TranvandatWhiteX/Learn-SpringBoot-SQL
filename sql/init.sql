CREATE TABLE permissions
(
    id BIGSERIAL PRIMARY KEY,
    description   VARCHAR(255) NOT NULL
);

CREATE TABLE roles
(
    id   BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL
);

CREATE TABLE role_permissions
(
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    CONSTRAINT role_permissions_pkey PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_permission FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    password      VARCHAR(255) NOT NULL,
    username      VARCHAR(75)  NOT NULL,
    address       VARCHAR(255) NOT NULL,
    date_of_birth TIMESTAMP    NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(12)  NOT NULL
);

CREATE TABLE user_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT user_roles_pkey PRIMARY KEY (role_id, user_id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO roles (role_name)
VALUES
    ('ADMIN'),
    ('MANAGER'),
    ('EDITOR'),
    ('VIEWER'),
    ('GUEST');

INSERT INTO permissions (description)
VALUES
    ('VIEW_DASHBOARD'),
    ('EDIT_PROFILE'),
    ('MANAGE_USERS'),
    ('MANAGE_ROLES'),
    ('VIEW_REPORTS'),
    ('EXPORT_DATA'),
    ('DELETE_RECORDS');

INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (1,1),
    (1,2),
    (1,3),
    (1,4),
    (1,5),
    (1,6),
    (1,7),
    (2,1),
    (2,5),
    (2,6),
    (2,7),
    (3,7),
    (4, 1),
    (4,6),
    (5,1);



