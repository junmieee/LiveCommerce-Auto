ALTER TABLE seller_monthly_summary ADD COLUMN seller_id_new BIGINT;

ALTER TABLE seller_monthly_summary
    ADD CONSTRAINT fk_sms_seller_new
    FOREIGN KEY (seller_id_new) REFERENCES sellers(id);

-- 데이터 마이그레이션 UPDATE 문 여기에 추가

ALTER TABLE seller_monthly_summary DROP COLUMN seller_id;
ALTER TABLE seller_monthly_summary RENAME COLUMN seller_id_new TO seller_id;
ALTER TABLE seller_monthly_summary ALTER COLUMN seller_id SET NOT NULL;