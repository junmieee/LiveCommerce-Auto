CREATE TABLE seller_monthly_summary (
  id BIGINT PRIMARY KEY,
  seller_id BIGINT REFERENCES users(id),
  year_month CHAR(7),
  total_sales DECIMAL(10,2),
  total_commission DECIMAL(10,2),
  last_updated_at TIMESTAMP
);
