BEGIN;

CREATE TABLE IF NOT EXISTS public.tenant (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  name text NOT NULL,
  category text NOT NULL, -- valor livre vindo da API/BAC
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_tenant_code
  ON public.tenant(code);

CREATE INDEX IF NOT EXISTS ix_tenant_category
  ON public.tenant(category);

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname = 'trg_tenant_updated_at'
  ) THEN
    CREATE TRIGGER trg_tenant_updated_at
    BEFORE UPDATE ON public.tenant
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

COMMIT;
