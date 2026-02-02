BEGIN;

CREATE TABLE IF NOT EXISTS public.item (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  tenant_id bigint NOT NULL,
  type text NOT NULL,
  name text NOT NULL,
  description text,
  sku text,
  unit_price_cents bigint NOT NULL DEFAULT 0,
  currency_code char(3) NOT NULL DEFAULT 'BRL',
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT ck_item_price_nonnegative CHECK (unit_price_cents >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_code
  ON public.item(code);

CREATE INDEX IF NOT EXISTS ix_item_tenant_id
  ON public.item(tenant_id);

CREATE INDEX IF NOT EXISTS ix_item_tenant_type
  ON public.item(tenant_id, type);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_tenant_sku
  ON public.item(tenant_id, lower(sku))
  WHERE sku IS NOT NULL;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_updated_at') THEN
    CREATE TRIGGER trg_item_updated_at
    BEFORE UPDATE ON public.item
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.item_detail (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item_id bigint NOT NULL,
  tenant_id bigint NOT NULL,
  tenant_category text,
  duration_minutes int,
  requires_staff boolean,
  service_rules jsonb,
  extra_attributes jsonb,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_detail_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE,
  CONSTRAINT fk_item_detail_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT uq_item_detail_item UNIQUE (item_id),
  CONSTRAINT ck_item_detail_duration
    CHECK (duration_minutes IS NULL OR duration_minutes > 0)
);

CREATE INDEX IF NOT EXISTS ix_item_detail_item_id
  ON public.item_detail(item_id);

CREATE INDEX IF NOT EXISTS ix_item_detail_tenant_id
  ON public.item_detail(tenant_id);

CREATE INDEX IF NOT EXISTS ix_item_detail_tenant_category
  ON public.item_detail(tenant_category);

CREATE INDEX IF NOT EXISTS ix_item_detail_service_rules_gin
  ON public.item_detail USING gin (service_rules);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_detail_updated_at') THEN
    CREATE TRIGGER trg_item_detail_updated_at
    BEFORE UPDATE ON public.item_detail
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.item_image (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  item_id bigint NOT NULL,
  tenant_id bigint NOT NULL,
  url text NOT NULL,
  alt_text text,
  sort_order int NOT NULL DEFAULT 0,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_image_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE,
  CONSTRAINT fk_item_image_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_image_code
  ON public.item_image(code);

CREATE INDEX IF NOT EXISTS ix_item_image_item_id
  ON public.item_image(item_id);

CREATE INDEX IF NOT EXISTS ix_item_image_tenant_id
  ON public.item_image(tenant_id);

CREATE INDEX IF NOT EXISTS ix_item_image_item_sort
  ON public.item_image(item_id, sort_order);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_image_updated_at') THEN
    CREATE TRIGGER trg_item_image_updated_at
    BEFORE UPDATE ON public.item_image
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

COMMIT;
