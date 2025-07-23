CREATE TABLE products (
  id BIGINT PRIMARY KEY,
  seller_id BIGINT REFERENCES users(id),
  name VARCHAR(255),
  description TEXT,
  price DECIMAL(10,2),
  stock_quantity INT,
  is_active BOOLEAN,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
