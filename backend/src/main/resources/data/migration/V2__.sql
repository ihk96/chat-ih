CREATE TABLE user_entity
(
    id                 VARCHAR(255) NOT NULL,
    username           VARCHAR(255) NOT NULL UNIQUE,
    password           VARCHAR(255) NOT NULL,
    roles              VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_entity PRIMARY KEY (id)
);
