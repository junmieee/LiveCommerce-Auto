CREATE TABLE order_items (
  id BIGINT PRIMARY KEY,
  order_id BIGINT REFERENCES orders(id),
  product_id BIGINT REFERENCES products(id),
  quantity INT,
  unit_price DECIMAL(10,2),
  total_price DECIMAL(10,2)
);
