BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS public.country (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  iso2        char(2)  NOT NULL,
  iso3        char(3)  NOT NULL,
  country        text     NOT NULL,
CONSTRAINT ux_country_iso2 UNIQUE (iso2),
  CONSTRAINT ux_country_iso3 UNIQUE (iso3),
  CONSTRAINT ux_country_country UNIQUE (country)
);

CREATE TABLE IF NOT EXISTS public.state (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  country_id  bigint   NOT NULL,
  acronym     char(2)  NOT NULL,
  state        text     NOT NULL,
  ibge_code   integer  NULL,
CONSTRAINT fk_state_country FOREIGN KEY (country_id) REFERENCES public.country(id) ON DELETE CASCADE,
  CONSTRAINT ux_state_country_acronym UNIQUE (country_id, acronym),
  CONSTRAINT ux_state_country_state UNIQUE (country_id, state)
);

CREATE TABLE IF NOT EXISTS public.city (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  state_id    bigint   NOT NULL,
  city        text     NOT NULL,
  ibge_code   integer  NULL,
  CONSTRAINT fk_city_state FOREIGN KEY (state_id) REFERENCES public.state(id) ON DELETE CASCADE,
  CONSTRAINT ux_city_state_city UNIQUE (state_id, city),
  CONSTRAINT ux_city_ibge UNIQUE (ibge_code)
);

CREATE INDEX IF NOT EXISTS ix_state_country_id ON public.state(country_id);
CREATE INDEX IF NOT EXISTS ix_city_state_id ON public.city(state_id);

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'set_updated_at') THEN
    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_country_updated_at') THEN
      CREATE TRIGGER trg_country_updated_at BEFORE UPDATE ON public.country
      FOR EACH ROW EXECUTE FUNCTION set_updated_at();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_state_updated_at') THEN
      CREATE TRIGGER trg_state_updated_at BEFORE UPDATE ON public.state
      FOR EACH ROW EXECUTE FUNCTION set_updated_at();
    END IF;
  END IF;
END $$;

INSERT INTO public.country (iso2, iso3, country)
VALUES ('BR', 'BRA', 'Brazil')
ON CONFLICT (iso2) DO NOTHING;

WITH br AS (
  SELECT id FROM public.country WHERE iso2 = 'BR'
)
INSERT INTO public.state (country_id, acronym, state, ibge_code)
SELECT br.id, s.acronym, s.state, s.ibge_code
FROM br
JOIN (VALUES
  ('AC','Acre',12),
  ('AL','Alagoas',27),
  ('AP','Amapá',16),
  ('AM','Amazonas',13),
  ('BA','Bahia',29),
  ('CE','Ceará',23),
  ('DF','Distrito Federal',53),
  ('ES','Espírito Santo',32),
  ('GO','Goiás',52),
  ('MA','Maranhão',21),
  ('MT','Mato Grosso',51),
  ('MS','Mato Grosso do Sul',50),
  ('MG','Minas Gerais',31),
  ('PA','Pará',15),
  ('PB','Paraíba',25),
  ('PR','Paraná',41),
  ('PE','Pernambuco',26),
  ('PI','Piauí',22),
  ('RJ','Rio de Janeiro',33),
  ('RN','Rio Grande do Norte',24),
  ('RS','Rio Grande do Sul',43),
  ('RO','Rondônia',11),
  ('RR','Roraima',14),
  ('SC','Santa Catarina',42),
  ('SP','São Paulo',35),
  ('SE','Sergipe',28),
  ('TO','Tocantins',17)
) AS s(acronym, state, ibge_code) ON true
ON CONFLICT (country_id, acronym) DO NOTHING;

CREATE TABLE IF NOT EXISTS public.address (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  street varchar(150) NOT NULL,
  number varchar(20),
  complement varchar(100),
  neighborhood varchar(100) NOT NULL,
  id_city bigint NOT NULL,
  postal_code varchar(20) NOT NULL,
  latitude numeric(10,7),
  longitude numeric(10,7),
  is_active boolean NOT NULL DEFAULT true,
CONSTRAINT fk_address_id_city
    FOREIGN KEY (id_city) REFERENCES public.city(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS ix_address_postal_code
  ON public.address(postal_code);

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
