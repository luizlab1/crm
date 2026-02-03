BEGIN;

CREATE TABLE IF NOT EXISTS public.person (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tenant_id bigint NOT NULL,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_person_code
  ON public.person(code);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_person_updated_at') THEN
    CREATE TRIGGER trg_person_updated_at
    BEFORE UPDATE ON public.person
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.person_physical (
  person_id bigint PRIMARY KEY,
  full_name varchar(150) NOT NULL,
  cpf varchar(14) NOT NULL,
  birth_date date,
  CONSTRAINT fk_person_physical_person
    FOREIGN KEY (person_id) REFERENCES public.person(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_person_physical_cpf
  ON public.person_physical(cpf);

CREATE TABLE IF NOT EXISTS public.person_legal (
  person_id bigint PRIMARY KEY,
  corporate_name varchar(150) NOT NULL,
  trade_name varchar(150),
  cnpj varchar(18) NOT NULL,
  CONSTRAINT fk_person_legal_person
    FOREIGN KEY (person_id) REFERENCES public.person(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_person_legal_cnpj
  ON public.person_legal(cnpj);

CREATE TABLE IF NOT EXISTS public.contact (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  person_id bigint NOT NULL,
  type varchar(30) NOT NULL,
  contact_value varchar(255) NOT NULL,
  is_primary boolean NOT NULL DEFAULT false,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_contact_person
    FOREIGN KEY (person_id) REFERENCES public.person(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS ix_contact_person_id
  ON public.contact(person_id);

CREATE INDEX IF NOT EXISTS ix_contact_type
  ON public.contact(type);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_contact_updated_at') THEN
    CREATE TRIGGER trg_contact_updated_at
    BEFORE UPDATE ON public.contact
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.person_address (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  person_id bigint NOT NULL,
  address_id bigint NOT NULL,
  type varchar(120) NOT NULL,
  is_primary boolean NOT NULL DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_person_address_person
    FOREIGN KEY (person_id) REFERENCES public.person(id) ON DELETE CASCADE,
  CONSTRAINT fk_person_address_address
    FOREIGN KEY (address_id) REFERENCES public.address(id) ON DELETE CASCADE,
  CONSTRAINT uq_person_address UNIQUE (person_id, address_id)
);

CREATE INDEX IF NOT EXISTS ix_person_address_person_id
  ON public.person_address(person_id);

CREATE INDEX IF NOT EXISTS ix_person_address_address_id
  ON public.person_address(address_id);

CREATE INDEX IF NOT EXISTS ix_person_address_type
  ON public.person_address(type);

COMMIT;
