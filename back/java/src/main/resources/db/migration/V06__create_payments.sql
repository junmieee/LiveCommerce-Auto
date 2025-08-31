CREATE TABLE payments (
  id BIGINT PRIMARY KEY,
  order_id BIGINT REFERENCES orders(id),
  payment_method VARCHAR(50),
  amount DECIMAL(10,2),
  paid_at TIMESTAMP,
  payment_status VARCHAR(50),
  transaction_id VARCHAR(255)
);
