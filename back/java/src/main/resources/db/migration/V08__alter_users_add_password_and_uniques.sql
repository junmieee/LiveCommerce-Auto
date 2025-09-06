-- Align users table with application requirements without rewriting history

-- 1) Ensure auto-increment behavior on id
CREATE SEQUENCE IF NOT EXISTS users_id_seq AS BIGINT;
SELECT setval('users_id_seq', GREATEST(COALESCE((SELECT MAX(id) FROM users), 0), 1));
ALTER TABLE users ALTER COLUMN id SET DEFAULT nextval('users_id_seq');

-- 2) Add password column for LOCAL provider accounts
ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(255);

-- 3) Strengthen provider constraint (set NOT NULL)
--    Backfill NULL providers to 'LOCAL' before applying NOT NULL (safe default for dev)
UPDATE users SET provider = 'LOCAL' WHERE provider IS NULL;
ALTER TABLE users ALTER COLUMN provider SET NOT NULL;

-- 4) Unique indexes to enforce logical constraints
--    a) Email unique case-insensitively (NULLs allowed)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE c.relkind = 'i' AND c.relname = 'users_email_lower_uk'
  ) THEN
    EXECUTE 'CREATE UNIQUE INDEX users_email_lower_uk ON users (LOWER(email))';
  END IF;
END $$;

--    b) Social identity unique (provider, provider_id) when provider_id present
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE c.relkind = 'i' AND c.relname = 'users_provider_pid_uk'
  ) THEN
    EXECUTE 'CREATE UNIQUE INDEX users_provider_pid_uk ON users (provider, provider_id) WHERE provider_id IS NOT NULL';
  END IF;
END $$;
