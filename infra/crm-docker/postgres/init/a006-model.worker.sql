BEGIN;

CREATE TABLE IF NOT EXISTS public.worker (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  person_id bigint NOT NULL,
  user_id bigint,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_worker_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_worker_person
    FOREIGN KEY (person_id) REFERENCES public.person(id) ON DELETE CASCADE,
  CONSTRAINT fk_worker_user
    FOREIGN KEY (user_id) REFERENCES public."user"(id) ON DELETE SET NULL,
  CONSTRAINT uq_worker_tenant_person UNIQUE (tenant_id, person_id),
  CONSTRAINT uq_worker_user UNIQUE (user_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_worker_code
  ON public.worker(code);

CREATE INDEX IF NOT EXISTS ix_worker_tenant_id
  ON public.worker(tenant_id);

CREATE INDEX IF NOT EXISTS ix_worker_person_id
  ON public.worker(person_id);

CREATE INDEX IF NOT EXISTS ix_worker_user_id
  ON public.worker(user_id);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_worker_updated_at') THEN
    CREATE TRIGGER trg_worker_updated_at
    BEFORE UPDATE ON public.worker
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

COMMIT;
