-- 1) products에 새 컬럼 추가
ALTER TABLE products ADD COLUMN seller_id_new BIGINT;

ALTER TABLE products
    ADD CONSTRAINT fk_products_seller_new
    FOREIGN KEY (seller_id_new) REFERENCES sellers(id);

-- 2) (데이터 마이그레이션 필요 시 여기에 UPDATE 추가)

-- 3) 컬럼 교체
ALTER TABLE products DROP COLUMN seller_id;
ALTER TABLE products RENAME COLUMN seller_id_new TO seller_id;
ALTER TABLE products ALTER COLUMN seller_id SET NOT NULL;