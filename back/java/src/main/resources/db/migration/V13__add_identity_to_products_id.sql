-- Ensure products.id auto generates (PostgreSQL)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.sequences WHERE sequence_name = 'products_id_seq'
  ) THEN
    CREATE SEQUENCE products_id_seq;
  END IF;
  PERFORM setval('products_id_seq', GREATEST(1, COALESCE((SELECT MAX(id) FROM products), 0) + 1));
  ALTER TABLE products ALTER COLUMN id SET DEFAULT nextval('products_id_seq');
END $$;
