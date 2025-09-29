ALTER TABLE products
    RENAME COLUMN description TO description_html;

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS thumbnail_image_key VARCHAR(255),
    ADD COLUMN IF NOT EXISTS main_image_keys TEXT NOT NULL DEFAULT '[]';
