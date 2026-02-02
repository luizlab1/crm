BEGIN;

CREATE TABLE IF NOT EXISTS public.customer (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  full_name varchar(150) NOT NULL,
  email varchar(255),
  phone varchar(30),
  document varchar(30),
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_customer_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_customer_code
  ON public.customer(code);

CREATE INDEX IF NOT EXISTS ix_customer_tenant_id
  ON public.customer(tenant_id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_customer_tenant_email
  ON public.customer(tenant_id, lower(email))
  WHERE email IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_customer_tenant_document
  ON public.customer(tenant_id, document)
  WHERE document IS NOT NULL;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_customer_updated_at') THEN
    CREATE TRIGGER trg_customer_updated_at
    BEFORE UPDATE ON public.customer
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

COMMIT;
