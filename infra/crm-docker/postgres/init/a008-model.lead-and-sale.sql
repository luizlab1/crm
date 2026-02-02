BEGIN;

CREATE TABLE IF NOT EXISTS public.lead (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  person_id bigint,
  customer_id bigint,
  status text NOT NULL DEFAULT 'NEW',
  source text,
  estimated_value_cents bigint,
  notes text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_lead_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_lead_person
    FOREIGN KEY (person_id) REFERENCES public.person(id) ON DELETE SET NULL,
  CONSTRAINT fk_lead_customer
    FOREIGN KEY (customer_id) REFERENCES public.customer(id) ON DELETE SET NULL,
  CONSTRAINT ck_lead_estimated_value_nonnegative
    CHECK (estimated_value_cents IS NULL OR estimated_value_cents >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_lead_code
  ON public.lead(code);

CREATE INDEX IF NOT EXISTS ix_lead_tenant_id
  ON public.lead(tenant_id);

CREATE INDEX IF NOT EXISTS ix_lead_status
  ON public.lead(tenant_id, status);

CREATE INDEX IF NOT EXISTS ix_lead_person_id
  ON public.lead(person_id);

CREATE INDEX IF NOT EXISTS ix_lead_customer_id
  ON public.lead(customer_id);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_lead_updated_at') THEN
    CREATE TRIGGER trg_lead_updated_at
    BEFORE UPDATE ON public.lead
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.sale (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  customer_id bigint NOT NULL,
  status text NOT NULL DEFAULT 'DRAFT',
  subtotal_cents bigint NOT NULL DEFAULT 0,
  discount_cents bigint NOT NULL DEFAULT 0,
  total_cents bigint NOT NULL DEFAULT 0,
  currency_code char(3) NOT NULL DEFAULT 'BRL',
  notes text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_sale_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_sale_customer
    FOREIGN KEY (customer_id) REFERENCES public.customer(id) ON DELETE RESTRICT,
  CONSTRAINT ck_sale_subtotal_nonnegative CHECK (subtotal_cents >= 0),
  CONSTRAINT ck_sale_discount_nonnegative CHECK (discount_cents >= 0),
  CONSTRAINT ck_sale_total_nonnegative CHECK (total_cents >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_sale_code
  ON public.sale(code);

CREATE INDEX IF NOT EXISTS ix_sale_tenant_id
  ON public.sale(tenant_id);

CREATE INDEX IF NOT EXISTS ix_sale_customer_id
  ON public.sale(customer_id);

CREATE INDEX IF NOT EXISTS ix_sale_status
  ON public.sale(tenant_id, status);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_sale_updated_at') THEN
    CREATE TRIGGER trg_sale_updated_at
    BEFORE UPDATE ON public.sale
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.sale_item (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  sale_id bigint NOT NULL,
  tenant_id bigint NOT NULL,
  item_id bigint NOT NULL,
  quantity int NOT NULL DEFAULT 1,
  unit_price_cents bigint NOT NULL,
  total_price_cents bigint NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_sale_item_sale
    FOREIGN KEY (sale_id) REFERENCES public.sale(id) ON DELETE CASCADE,
  CONSTRAINT fk_sale_item_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_sale_item_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE RESTRICT,
  CONSTRAINT ck_sale_item_quantity_positive CHECK (quantity > 0),
  CONSTRAINT ck_sale_item_unit_price_nonnegative CHECK (unit_price_cents >= 0),
  CONSTRAINT ck_sale_item_total_price_nonnegative CHECK (total_price_cents >= 0),
  CONSTRAINT uq_sale_item UNIQUE (sale_id, item_id)
);

CREATE INDEX IF NOT EXISTS ix_sale_item_sale_id
  ON public.sale_item(sale_id);

CREATE INDEX IF NOT EXISTS ix_sale_item_tenant_id
  ON public.sale_item(tenant_id);

CREATE INDEX IF NOT EXISTS ix_sale_item_item_id
  ON public.sale_item(item_id);

COMMIT;
