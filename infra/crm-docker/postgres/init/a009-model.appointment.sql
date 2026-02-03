BEGIN;

CREATE TABLE IF NOT EXISTS public.schedule (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  customer_id bigint NOT NULL,
  schedule_id bigint,
  name text NOT NULL,
  description text,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_schedule_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_schedule_customer
    FOREIGN KEY (customer_id) REFERENCES public.customer(id) ON DELETE RESTRICT,
  CONSTRAINT fk_schedule_parent
    FOREIGN KEY (schedule_id) REFERENCES public.schedule(id) ON DELETE SET NULL,
  CONSTRAINT ck_schedule_parent_not_self
    CHECK (schedule_id IS NULL OR schedule_id <> id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_schedule_code
  ON public.schedule(code);

CREATE INDEX IF NOT EXISTS ix_schedule_tenant_id
  ON public.schedule(tenant_id);

CREATE INDEX IF NOT EXISTS ix_schedule_customer_id
  ON public.schedule(customer_id);

CREATE INDEX IF NOT EXISTS ix_schedule_schedule_id
  ON public.schedule(schedule_id);

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
  status text NOT NULL DEFAULT 'SCHEDULED',
  scheduled_at timestamptz NOT NULL,
  started_at timestamptz,
  finished_at timestamptz,
  total_cents bigint,
  notes text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_appointment_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT ck_appointment_total_nonnegative
    CHECK (total_cents IS NULL OR total_cents >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_appointment_code
  ON public.appointment(code);

CREATE INDEX IF NOT EXISTS ix_appointment_tenant_id
  ON public.appointment(tenant_id);

CREATE INDEX IF NOT EXISTS ix_appointment_status
  ON public.appointment(tenant_id, status);

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

CREATE TABLE IF NOT EXISTS public.appointment_order (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  appointment_id bigint NOT NULL,
  order_id bigint NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_appointment_order_appointment
    FOREIGN KEY (appointment_id) REFERENCES public.appointment(id) ON DELETE CASCADE,
  CONSTRAINT fk_appointment_order_order
    FOREIGN KEY (order_id) REFERENCES public."order"(id) ON DELETE RESTRICT,
  CONSTRAINT uq_appointment_order UNIQUE (appointment_id, order_id)
);

CREATE INDEX IF NOT EXISTS ix_appointment_order_appointment_id
  ON public.appointment_order(appointment_id);

CREATE INDEX IF NOT EXISTS ix_appointment_order_order_id
  ON public.appointment_order(order_id);

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

CREATE TABLE IF NOT EXISTS public.lead_order (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  lead_id bigint NOT NULL,
  order_id bigint NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_lead_order_lead
    FOREIGN KEY (lead_id) REFERENCES public.lead(id) ON DELETE CASCADE,
  CONSTRAINT fk_lead_order_order
    FOREIGN KEY (order_id) REFERENCES public."order"(id) ON DELETE CASCADE,
  CONSTRAINT uq_lead_order UNIQUE (lead_id, order_id)
);

CREATE INDEX IF NOT EXISTS ix_lead_order_lead_id
  ON public.lead_order(lead_id);

CREATE INDEX IF NOT EXISTS ix_lead_order_order_id
  ON public.lead_order(order_id);

COMMIT;
