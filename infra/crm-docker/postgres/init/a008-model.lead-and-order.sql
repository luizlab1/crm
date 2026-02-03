BEGIN;

CREATE TABLE IF NOT EXISTS public.lead (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  flow_id bigint NOT NULL,
  customer_id bigint,
  status text NOT NULL DEFAULT 'NEW',
  source text,
  estimated_value_cents bigint,
  notes text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_lead_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_lead_flow
    FOREIGN KEY (flow_id) REFERENCES public.pipeline_flow(id) ON DELETE RESTRICT,
  CONSTRAINT fk_lead_customer
    FOREIGN KEY (customer_id) REFERENCES public.customer(id) ON DELETE SET NULL,
  CONSTRAINT ck_lead_estimated_value_nonnegative
    CHECK (estimated_value_cents IS NULL OR estimated_value_cents >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_lead_code
  ON public.lead(code);

CREATE INDEX IF NOT EXISTS ix_lead_tenant_id
  ON public.lead(tenant_id);

CREATE INDEX IF NOT EXISTS ix_lead_flow_id
  ON public.lead(flow_id);

CREATE INDEX IF NOT EXISTS ix_lead_status
  ON public.lead(tenant_id, status);

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

CREATE TABLE IF NOT EXISTS public.lead_message (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  lead_id bigint NOT NULL,
  message text NOT NULL,
  channel text,
  created_by_user_id bigint,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_lead_message_lead
    FOREIGN KEY (lead_id) REFERENCES public.lead(id) ON DELETE CASCADE,
  CONSTRAINT fk_lead_message_user
    FOREIGN KEY (created_by_user_id) REFERENCES public."user"(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS ix_lead_message_lead_id
  ON public.lead_message(lead_id);

CREATE TABLE IF NOT EXISTS public."order" (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  customer_id bigint NOT NULL,
  user_id bigint NOT NULL,
  status text NOT NULL DEFAULT 'DRAFT',
  subtotal_cents bigint NOT NULL DEFAULT 0,
  discount_cents bigint NOT NULL DEFAULT 0,
  total_cents bigint NOT NULL DEFAULT 0,
  currency_code char(3) NOT NULL DEFAULT 'BRL',
  notes text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_order_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_order_customer
    FOREIGN KEY (customer_id) REFERENCES public.customer(id) ON DELETE RESTRICT,
  CONSTRAINT fk_order_user
    FOREIGN KEY (user_id) REFERENCES public."user"(id) ON DELETE RESTRICT,
  CONSTRAINT ck_order_subtotal_nonnegative CHECK (subtotal_cents >= 0),
  CONSTRAINT ck_order_discount_nonnegative CHECK (discount_cents >= 0),
  CONSTRAINT ck_order_total_nonnegative CHECK (total_cents >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_order_code
  ON public."order"(code);

CREATE INDEX IF NOT EXISTS ix_order_tenant_id
  ON public."order"(tenant_id);

CREATE INDEX IF NOT EXISTS ix_order_customer_id
  ON public."order"(customer_id);

CREATE INDEX IF NOT EXISTS ix_order_user_id
  ON public."order"(user_id);

CREATE INDEX IF NOT EXISTS ix_order_status
  ON public."order"(tenant_id, status);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_order_updated_at') THEN
    CREATE TRIGGER trg_order_updated_at
    BEFORE UPDATE ON public."order"
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.order_item (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  order_id bigint NOT NULL,
  tenant_id bigint NOT NULL,
  item_id bigint NOT NULL,
  quantity int NOT NULL DEFAULT 1,
  unit_price_cents bigint NOT NULL,
  total_price_cents bigint NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id) REFERENCES public."order"(id) ON DELETE CASCADE,
  CONSTRAINT fk_order_item_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_order_item_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE RESTRICT,
  CONSTRAINT ck_order_item_quantity_positive CHECK (quantity > 0),
  CONSTRAINT ck_order_item_unit_price_nonnegative CHECK (unit_price_cents >= 0),
  CONSTRAINT ck_order_item_total_price_nonnegative CHECK (total_price_cents >= 0),
  CONSTRAINT uq_order_item UNIQUE (order_id, item_id)
);

CREATE INDEX IF NOT EXISTS ix_order_item_order_id
  ON public.order_item(order_id);

CREATE INDEX IF NOT EXISTS ix_order_item_tenant_id
  ON public.order_item(tenant_id);

CREATE INDEX IF NOT EXISTS ix_order_item_item_id
  ON public.order_item(item_id);

CREATE TABLE IF NOT EXISTS public.order_item_option (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  order_item_id bigint NOT NULL,
  option_name text NOT NULL,
  option_value text,
  price_cents bigint NOT NULL DEFAULT 0,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_order_item_option_order_item
    FOREIGN KEY (order_item_id) REFERENCES public.order_item(id) ON DELETE CASCADE,
  CONSTRAINT ck_order_item_option_price_nonnegative CHECK (price_cents >= 0)
);

CREATE INDEX IF NOT EXISTS ix_order_item_option_order_item_id
  ON public.order_item_option(order_item_id);

CREATE TABLE IF NOT EXISTS public.order_item_additional (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  order_item_id bigint NOT NULL,
  name text NOT NULL,
  description text,
  price_cents bigint NOT NULL DEFAULT 0,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_order_item_additional_order_item
    FOREIGN KEY (order_item_id) REFERENCES public.order_item(id) ON DELETE CASCADE,
  CONSTRAINT ck_order_item_additional_price_nonnegative CHECK (price_cents >= 0)
);

CREATE INDEX IF NOT EXISTS ix_order_item_additional_order_item_id
  ON public.order_item_additional(order_item_id);

COMMIT;
