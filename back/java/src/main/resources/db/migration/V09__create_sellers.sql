-- V09__create_sellers.sql
CREATE TABLE sellers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    business_number VARCHAR(100) UNIQUE,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(30),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',  -- PENDING/ACTIVE/SUSPENDED
    commission_rate NUMERIC(5,2) DEFAULT 0.00,
    settlement_cycle VARCHAR(20) DEFAULT 'MONTHLY',
    payout_day SMALLINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP
);

CREATE TABLE seller_members (
    seller_id BIGINT NOT NULL REFERENCES sellers(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    role VARCHAR(30) NOT NULL DEFAULT 'OWNER',
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    joined_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (seller_id, user_id)
);