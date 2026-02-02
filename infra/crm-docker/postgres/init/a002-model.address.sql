BEGIN;

CREATE TABLE IF NOT EXISTS public.country (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code varchar(3) NOT NULL,
  name varchar(100) NOT NULL,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_country_code
  ON public.country(code);

CREATE TABLE IF NOT EXISTS public.state (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  country_id bigint NOT NULL,
  code varchar(10) NOT NULL,
  name varchar(100) NOT NULL,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_state_country
    FOREIGN KEY (country_id) REFERENCES public.country(id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_state_country_code
  ON public.state(country_id, code);

CREATE TABLE IF NOT EXISTS public.address (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  street varchar(150) NOT NULL,
  number varchar(20),
  complement varchar(100),
  neighborhood varchar(100) NOT NULL,
  city varchar(100) NOT NULL,
  state_id bigint NOT NULL,
  postal_code varchar(20) NOT NULL,
  latitude numeric(10,7),
  longitude numeric(10,7),
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_address_state
    FOREIGN KEY (state_id) REFERENCES public.state(id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_address_code
  ON public.address(code);

CREATE INDEX IF NOT EXISTS ix_address_postal_code
  ON public.address(postal_code);

CREATE INDEX IF NOT EXISTS ix_address_city
  ON public.address(city);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_country_updated_at') THEN
    CREATE TRIGGER trg_country_updated_at
    BEFORE UPDATE ON public.country
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_state_updated_at') THEN
    CREATE TRIGGER trg_state_updated_at
    BEFORE UPDATE ON public.state
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_address_updated_at') THEN
    CREATE TRIGGER trg_address_updated_at
    BEFORE UPDATE ON public.address
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

COMMIT;
