CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  username varchar(50) UNIQUE NOT NULL,
  email varchar(255) UNIQUE NOT NULL,
  password_hash varchar(255) NOT NULL,
  display_name varchar(100),
  avatar_key varchar(512),
  bio text,
  email_verified boolean DEFAULT false,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid REFERENCES users(id) ON DELETE CASCADE,
  token_hash varchar(255) NOT NULL,
  expires_at timestamptz NOT NULL,
  revoked boolean DEFAULT false
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_refresh_token_hash ON refresh_tokens(token_hash);

ALTER TABLE refresh_tokens
  ADD COLUMN IF NOT EXISTS token_id uuid UNIQUE DEFAULT gen_random_uuid();

CREATE INDEX IF NOT EXISTS idx_refresh_token_token_id ON refresh_tokens(token_id);

UPDATE refresh_tokens SET token_id = gen_random_uuid() WHERE token_id IS NULL;
