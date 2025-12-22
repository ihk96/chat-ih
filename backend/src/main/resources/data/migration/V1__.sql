CREATE TABLE ai_providers
(
    id                 VARCHAR(255) NOT NULL,
    name               VARCHAR(255),
    provider           SMALLINT,
    base_url           VARCHAR(255),
    api_key            VARCHAR(255),
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_ai_providers PRIMARY KEY (id)
);

CREATE TABLE chat_sessions
(
    id                 VARCHAR(255) NOT NULL,
    user_id            VARCHAR(255),
    title              VARCHAR(255),
    messages           JSONB,
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_chat_sessions PRIMARY KEY (id)
);

CREATE TABLE ll_models
(
    id                 VARCHAR(255) NOT NULL,
    public_name        VARCHAR(255),
    origin_name        VARCHAR(255),
    provider_id        VARCHAR(255),
    completion_url     VARCHAR(255),
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_ll_models PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                 VARCHAR(255) NOT NULL,
    username           VARCHAR(255) NOT NULL,
    password           VARCHAR(255) NOT NULL,
    roles              VARCHAR(255) NOT NULL,
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);