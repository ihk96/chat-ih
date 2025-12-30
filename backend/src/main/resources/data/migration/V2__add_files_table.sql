CREATE TABLE files
(
    id                 VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    storage_path       VARCHAR(255) NOT NULL,
    content_type       VARCHAR(255) NOT NULL,
    size               BIGINT       NOT NULL,
    user_id            VARCHAR(255) NOT NULL,
    is_used            BOOLEAN      NOT NULL DEFAULT FALSE,
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_files PRIMARY KEY (id)
);
