BEGIN;

CREATE TABLE IF NOT EXISTS public.tenant (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  name varchar(120) NOT NULL,
  category varchar(60) NOT NULL,
  person_id bigint NULL,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_tenant_person
    FOREIGN KEY (person_id) REFERENCES public.person(id)
    ON DELETE RESTRICT
    DEFERRABLE INITIALLY DEFERRED
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_tenant_code ON public.tenant(code);
CREATE INDEX IF NOT EXISTS ix_tenant_category ON public.tenant(category);
CREATE INDEX IF NOT EXISTS ix_tenant_person_id ON public.tenant(person_id);
CREATE INDEX IF NOT EXISTS ix_person_tenant_id ON public.person(tenant_id);

ALTER TABLE public.person
  ADD CONSTRAINT fk_person_tenant
  FOREIGN KEY (tenant_id)
  REFERENCES public.tenant(id)
  ON DELETE CASCADE
  DEFERRABLE INITIALLY DEFERRED;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_tenant_updated_at') THEN
    CREATE TRIGGER trg_tenant_updated_at
    BEFORE UPDATE ON public.tenant
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

COMMIT;
