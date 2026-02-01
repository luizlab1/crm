BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS public."user" (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  email text NOT NULL,
  password_hash text NOT NULL,
  full_name text,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_user_email
  ON public."user"(lower(email));

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname = 'trg_user_updated_at'
  ) THEN
    CREATE TRIGGER trg_user_updated_at
    BEFORE UPDATE ON public."user"
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public."role" (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name text NOT NULL,
  description text,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_role_name
  ON public."role"(lower(name));

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname = 'trg_role_updated_at'
  ) THEN
    CREATE TRIGGER trg_role_updated_at
    BEFORE UPDATE ON public."role"
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.grantt (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  code text NOT NULL,
  description text,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_grantt_code
  ON public.grantt(lower(code));

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname = 'trg_grantt_updated_at'
  ) THEN
    CREATE TRIGGER trg_grantt_updated_at
    BEFORE UPDATE ON public.grantt
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.user_role (
  user_id uuid NOT NULL,
  role_id uuid NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_role_user
    FOREIGN KEY (user_id) REFERENCES public."user"(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_role_role
    FOREIGN KEY (role_id) REFERENCES public."role"(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS ix_user_role_role_id
  ON public.user_role(role_id);

CREATE TABLE IF NOT EXISTS public.user_grant (
  user_id uuid NOT NULL,
  grantt_id uuid NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, grantt_id),
  CONSTRAINT fk_user_grant_user
    FOREIGN KEY (user_id) REFERENCES public."user"(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_grant_grantt
    FOREIGN KEY (grantt_id) REFERENCES public.grantt(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS ix_user_grant_grantt_id
  ON public.user_grant(grantt_id);

COMMIT;
