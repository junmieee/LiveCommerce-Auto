CREATE TABLE users (
  id BIGINT PRIMARY KEY,
  email VARCHAR(255),
  name VARCHAR(100),
  provider VARCHAR(50),
  provider_id VARCHAR(255),
  profile_image TEXT,
  created_at TIMESTAMP,
  last_login_at TIMESTAMP,
  is_active BOOLEAN,
  is_seller BOOLEAN,
  company_name VARCHAR(255),
  business_number VARCHAR(100),
  contact_email VARCHAR(255)
);
