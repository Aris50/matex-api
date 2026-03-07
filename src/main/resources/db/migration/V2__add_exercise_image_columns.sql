ALTER TABLE exercises
    ADD COLUMN image_path VARCHAR(500) NULL,
    ADD COLUMN image_original_name VARCHAR(255) NULL,
    ADD COLUMN image_content_type VARCHAR(100) NULL,
    ADD COLUMN image_size_bytes BIGINT NULL;