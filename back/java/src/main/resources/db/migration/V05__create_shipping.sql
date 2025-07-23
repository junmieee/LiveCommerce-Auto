CREATE TABLE shipping (
  id BIGINT PRIMARY KEY,
  order_id BIGINT REFERENCES orders(id),
  recipient_name VARCHAR(100),
  address TEXT,
  phone_number VARCHAR(20),
  tracking_number VARCHAR(100),
  carrier VARCHAR(50),
  shipping_status VARCHAR(50),
  shipped_at TIMESTAMP,
  delivered_at TIMESTAMP
);
