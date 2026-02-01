BEGIN;

CREATE TABLE IF NOT EXISTS public.schedule (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  name text NOT NULL,
  description text,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_schedule_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_schedule_code ON public.schedule(code);
CREATE INDEX IF NOT EXISTS ix_schedule_tenant_id ON public.schedule(tenant_id);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_schedule_updated_at') THEN
    CREATE TRIGGER trg_schedule_updated_at
    BEFORE UPDATE ON public.schedule
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.appointment (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  schedule_id bigint NOT NULL,
  customer_id bigint NOT NULL,
  status text NOT NULL DEFAULT 'SCHEDULED', -- valor livre vindo da API/BAC
  scheduled_at timestamptz NOT NULL,
  started_at timestamptz,
  finished_at timestamptz,
  total_cents bigint,
  notes text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_appointment_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_appointment_schedule
    FOREIGN KEY (schedule_id) REFERENCES public.schedule(id) ON DELETE RESTRICT,
  CONSTRAINT fk_appointment_customer
    FOREIGN KEY (customer_id) REFERENCES public.customer(id) ON DELETE RESTRICT,
  CONSTRAINT ck_appointment_total_nonnegative
    CHECK (total_cents IS NULL OR total_cents >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_appointment_code ON public.appointment(code);
CREATE INDEX IF NOT EXISTS ix_appointment_tenant_id ON public.appointment(tenant_id);
CREATE INDEX IF NOT EXISTS ix_appointment_schedule_id ON public.appointment(schedule_id);
CREATE INDEX IF NOT EXISTS ix_appointment_customer_id ON public.appointment(customer_id);
CREATE INDEX IF NOT EXISTS ix_appointment_status ON public.appointment(tenant_id, status);
CREATE INDEX IF NOT EXISTS ix_appointment_scheduled_at
  ON public.appointment(tenant_id, scheduled_at);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_appointment_updated_at') THEN
    CREATE TRIGGER trg_appointment_updated_at
    BEFORE UPDATE ON public.appointment
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.appointment_item (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  appointment_id bigint NOT NULL,
  item_id bigint NOT NULL,
  quantity int NOT NULL DEFAULT 1,
  unit_price_cents bigint NOT NULL DEFAULT 0,
  total_cents bigint NOT NULL DEFAULT 0,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_appointment_item_appointment
    FOREIGN KEY (appointment_id) REFERENCES public.appointment(id) ON DELETE CASCADE,
  CONSTRAINT fk_appointment_item_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE RESTRICT,
  CONSTRAINT uq_appointment_item UNIQUE (appointment_id, item_id),
  CONSTRAINT ck_appointment_item_qty CHECK (quantity > 0),
  CONSTRAINT ck_appointment_item_price CHECK (unit_price_cents >= 0 AND total_cents >= 0)
);

CREATE INDEX IF NOT EXISTS ix_appointment_item_appointment_id
  ON public.appointment_item(appointment_id);

CREATE INDEX IF NOT EXISTS ix_appointment_item_item_id
  ON public.appointment_item(item_id);

CREATE TABLE IF NOT EXISTS public.appointment_note (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  appointment_id bigint NOT NULL,
  note text NOT NULL,
  created_by_user_id bigint,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_appointment_note_appointment
    FOREIGN KEY (appointment_id) REFERENCES public.appointment(id) ON DELETE CASCADE,
  CONSTRAINT fk_appointment_note_user
    FOREIGN KEY (created_by_user_id) REFERENCES public."user"(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS ix_appointment_note_appointment_id
  ON public.appointment_note(appointment_id);

COMMIT;
