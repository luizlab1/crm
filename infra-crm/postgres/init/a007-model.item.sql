BEGIN;

CREATE TABLE IF NOT EXISTS public.unit_of_measure (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code        varchar(20)  NOT NULL,
  name        varchar(100) NOT NULL,
  symbol      varchar(10),
  is_active   boolean      NOT NULL DEFAULT true,
  created_at  timestamptz  NOT NULL DEFAULT now(),
  updated_at  timestamptz  NOT NULL DEFAULT now(),
  CONSTRAINT ux_unit_of_measure_code UNIQUE (code)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_unit_of_measure_lower_code
  ON public.unit_of_measure (lower(code));

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_unit_of_measure_updated_at') THEN
    CREATE TRIGGER trg_unit_of_measure_updated_at
    BEFORE UPDATE ON public.unit_of_measure
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

INSERT INTO public.unit_of_measure (code, name, symbol) VALUES
('UN', 'Unidade', 'un'),
('KG', 'Quilograma', 'kg'),
('G',  'Grama', 'g'),
('L',  'Litro', 'L'),
('ML', 'Mililitro', 'mL'),
('M',  'Metro', 'm'),
('CM', 'Centímetro', 'cm'),
('MM', 'Milímetro', 'mm')
ON CONFLICT (code) DO NOTHING;

CREATE TABLE IF NOT EXISTS public.item_category (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tenant_id   bigint        NOT NULL,
  name        varchar(150)  NOT NULL,
  show_on_site boolean      NOT NULL DEFAULT true,
  created_at  timestamptz   NOT NULL DEFAULT now(),
  updated_at  timestamptz   NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_category_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_category_tenant_lower_name
  ON public.item_category (tenant_id, lower(name));

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_category_updated_at') THEN
    CREATE TRIGGER trg_item_category_updated_at
    BEFORE UPDATE ON public.item_category
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.item (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code        uuid          NOT NULL DEFAULT gen_random_uuid(),
  tenant_id   bigint        NOT NULL,
  category_id bigint,
  type        varchar(60)   NOT NULL,
  name        varchar(150)  NOT NULL,
  sku         varchar(100),
  is_active   boolean       NOT NULL DEFAULT true,
  created_at  timestamptz   NOT NULL DEFAULT now(),
  updated_at  timestamptz   NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_item_category
    FOREIGN KEY (category_id) REFERENCES public.item_category(id) ON DELETE SET NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_code
  ON public.item(code);

CREATE INDEX IF NOT EXISTS ix_item_tenant_id
  ON public.item(tenant_id);

CREATE INDEX IF NOT EXISTS ix_item_category_id
  ON public.item(category_id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_tenant_lower_sku
  ON public.item (tenant_id, lower(sku))
  WHERE sku IS NOT NULL;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_updated_at') THEN
    CREATE TRIGGER trg_item_updated_at
    BEFORE UPDATE ON public.item
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.item_product_datasheet (
  id                  bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item_id             bigint        NOT NULL,
  description         varchar(1000),
  unit_price_cents    bigint        NOT NULL DEFAULT 0,
  currency_code       varchar(3)    NOT NULL DEFAULT 'BRL',
  unit_of_measure_id  bigint,
  weight_kg           numeric(12,3),
  volume_m3           numeric(12,6),
  density_kg_m3       numeric(12,3),
  height_cm           numeric(12,3),
  width_cm            numeric(12,3),
  length_cm           numeric(12,3),
  created_at          timestamptz   NOT NULL DEFAULT now(),
  updated_at          timestamptz   NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_product_datasheet_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE,
  CONSTRAINT fk_item_product_datasheet_uom
    FOREIGN KEY (unit_of_measure_id) REFERENCES public.unit_of_measure(id) ON DELETE SET NULL,
  CONSTRAINT uq_item_product_datasheet_item UNIQUE (item_id),
  CONSTRAINT ck_item_product_price_nonnegative CHECK (unit_price_cents >= 0),
  CONSTRAINT ck_item_product_metrics_nonnegative CHECK (
    (weight_kg IS NULL OR weight_kg >= 0) AND
    (volume_m3 IS NULL OR volume_m3 >= 0) AND
    (density_kg_m3 IS NULL OR density_kg_m3 >= 0) AND
    (height_cm IS NULL OR height_cm >= 0) AND
    (width_cm IS NULL OR width_cm >= 0) AND
    (length_cm IS NULL OR length_cm >= 0)
  )
);

CREATE INDEX IF NOT EXISTS ix_item_product_datasheet_item_id
  ON public.item_product_datasheet(item_id);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_product_datasheet_updated_at') THEN
    CREATE TRIGGER trg_item_product_datasheet_updated_at
    BEFORE UPDATE ON public.item_product_datasheet
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.item_service_datasheet (
  id                 bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item_id            bigint        NOT NULL,
  description        varchar(1000),
  unit_price_cents   bigint        NOT NULL DEFAULT 0,
  currency_code      varchar(3)    NOT NULL DEFAULT 'BRL',
  duration_minutes   integer,
  requires_staff     boolean       NOT NULL DEFAULT false,
  buffer_minutes     integer,
  created_at         timestamptz   NOT NULL DEFAULT now(),
  updated_at         timestamptz   NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_service_datasheet_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE,
  CONSTRAINT uq_item_service_datasheet_item UNIQUE (item_id),
  CONSTRAINT ck_item_service_price_nonnegative CHECK (unit_price_cents >= 0),
  CONSTRAINT ck_item_service_duration CHECK (duration_minutes IS NULL OR duration_minutes > 0),
  CONSTRAINT ck_item_service_buffer CHECK (buffer_minutes IS NULL OR buffer_minutes >= 0)
);

CREATE INDEX IF NOT EXISTS ix_item_service_datasheet_item_id
  ON public.item_service_datasheet(item_id);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_service_datasheet_updated_at') THEN
    CREATE TRIGGER trg_item_service_datasheet_updated_at
    BEFORE UPDATE ON public.item_service_datasheet
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.item_image (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code        uuid           NOT NULL DEFAULT gen_random_uuid(),
  item_id     bigint         NOT NULL,
  url         varchar(1000)  NOT NULL,
  alt_text    varchar(255),
  sort_order  integer        NOT NULL DEFAULT 0,
  is_active   boolean        NOT NULL DEFAULT true,
  created_at  timestamptz    NOT NULL DEFAULT now(),
  updated_at  timestamptz    NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_image_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_image_code
  ON public.item_image(code);

CREATE INDEX IF NOT EXISTS ix_item_image_item_id
  ON public.item_image(item_id);

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

CREATE TABLE IF NOT EXISTS public.item_tag (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item_id     bigint        NOT NULL,
  tag         varchar(100)  NOT NULL,
  created_at  timestamptz   NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_tag_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_tag_item_lower_tag
  ON public.item_tag (item_id, lower(tag));

CREATE INDEX IF NOT EXISTS ix_item_tag_item_id
  ON public.item_tag(item_id);

CREATE INDEX IF NOT EXISTS ix_item_tag_lower_tag
  ON public.item_tag(lower(tag));

CREATE TABLE IF NOT EXISTS public.item_option (
  id                 bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item_id            bigint        NOT NULL,
  name               varchar(150)  NOT NULL,
  price_delta_cents  bigint        NOT NULL DEFAULT 0,
  is_active          boolean       NOT NULL DEFAULT true,
  created_at         timestamptz   NOT NULL DEFAULT now(),
  updated_at         timestamptz   NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_option_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE,
  CONSTRAINT ck_item_option_price CHECK (price_delta_cents >= 0)
);

CREATE INDEX IF NOT EXISTS ix_item_option_item_id
  ON public.item_option(item_id);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_option_updated_at') THEN
    CREATE TRIGGER trg_item_option_updated_at
    BEFORE UPDATE ON public.item_option
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.item_additional (
  id           bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item_id      bigint        NOT NULL,
  name         varchar(150)  NOT NULL,
  price_cents  bigint        NOT NULL DEFAULT 0,
  is_active    boolean       NOT NULL DEFAULT true,
  created_at   timestamptz   NOT NULL DEFAULT now(),
  updated_at   timestamptz   NOT NULL DEFAULT now(),
  CONSTRAINT fk_item_additional_item
    FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE CASCADE,
  CONSTRAINT ck_item_additional_price CHECK (price_cents >= 0)
);

CREATE INDEX IF NOT EXISTS ix_item_additional_item_id
  ON public.item_additional(item_id);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_additional_updated_at') THEN
    CREATE TRIGGER trg_item_additional_updated_at
    BEFORE UPDATE ON public.item_additional
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

COMMIT;
