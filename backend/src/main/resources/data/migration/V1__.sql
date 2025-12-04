CREATE TABLE chat_session_entity
(
    id                 VARCHAR(255) NOT NULL,
    user_id            VARCHAR(255),
    messages           JSONB,
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_chatsessionentity PRIMARY KEY (id)
);

CREATE TABLE ll_model
(
    id             VARCHAR(255) NOT NULL,
    public_name    VARCHAR(255),
    origin_name    VARCHAR(255),
    provider       SMALLINT,
    base_url       VARCHAR(255),
    api_key        VARCHAR(255),
    completion_url VARCHAR(255),
    CONSTRAINT pk_ll_model PRIMARY KEY (id)
);