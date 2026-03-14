BEGIN;

CREATE TABLE IF NOT EXISTS public.tenant (
  id                bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  parent_tenant_id  bigint       NULL,
  code              uuid         NOT NULL DEFAULT gen_random_uuid(),
  name              varchar(120) NOT NULL,
  category          varchar(60)  NOT NULL,
  is_active         boolean      NOT NULL DEFAULT true,
  created_at        timestamptz  NOT NULL DEFAULT now(),
  updated_at        timestamptz  NOT NULL DEFAULT now(),
  CONSTRAINT fk_tenant_parent
    FOREIGN KEY (parent_tenant_id) REFERENCES public.tenant(id) ON DELETE SET NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_tenant_code
  ON public.tenant(code);

CREATE INDEX IF NOT EXISTS ix_tenant_category
  ON public.tenant(category);

CREATE INDEX IF NOT EXISTS ix_tenant_parent_tenant_id
  ON public.tenant(parent_tenant_id);

CREATE INDEX IF NOT EXISTS ix_person_tenant_id
  ON public.person(tenant_id);

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'fk_person_tenant'
  ) THEN
    ALTER TABLE public.person
      ADD CONSTRAINT fk_person_tenant
      FOREIGN KEY (tenant_id)
      REFERENCES public.tenant(id)
      ON DELETE CASCADE
      DEFERRABLE INITIALLY DEFERRED;
  END IF;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_tenant_updated_at') THEN
    CREATE TRIGGER trg_tenant_updated_at
    BEFORE UPDATE ON public.tenant
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

COMMIT;