CREATE TABLE orders (
  id BIGINT PRIMARY KEY,
  user_id BIGINT REFERENCES users(id),
  total_price DECIMAL(10,2),
  order_status VARCHAR(50),
  ordered_at TIMESTAMP,
  confirmed_at TIMESTAMP,
  cancelled_at TIMESTAMP
);
