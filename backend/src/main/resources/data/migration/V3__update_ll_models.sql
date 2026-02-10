ALTER TABLE ll_models
    ALTER COLUMN provider_id SET NOT NULL;

ALTER TABLE ll_models
    ALTER COLUMN origin_name SET NOT NULL;

ALTER TABLE ll_models
    ALTER COLUMN public_name SET NOT NULL;

ALTER TABLE ll_models
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE ll_models
    ADD COLUMN extra_config TEXT;

ALTER TABLE ll_models
    DROP COLUMN completion_url;

ALTER TABLE ll_models
    ALTER COLUMN status DROP DEFAULT;
