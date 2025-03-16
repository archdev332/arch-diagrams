DROP TABLE IF EXISTS accounts;
CREATE TABLE IF NOT EXISTS accounts
(
  id         SERIAL PRIMARY KEY,
  user_id    BIGINT NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  balance    VARCHAR(255) NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);
ALTER TABLE accounts REPLICA IDENTITY FULL;
ALTER PUBLICATION dbz_publication ADD TABLE public.accounts;

SELECT conname, conrelid::regclass AS table_name, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'accounts'::regclass;
