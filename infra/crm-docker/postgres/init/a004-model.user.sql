BEGIN;

CREATE TABLE IF NOT EXISTS public."user" (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tenant_id bigint NOT NULL,
  person_id bigint,
  code uuid NOT NULL DEFAULT gen_random_uuid(),
  email varchar(255) NOT NULL,
  password_hash varchar(255) NOT NULL,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_user_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_person
    FOREIGN KEY (person_id) REFERENCES public.person(id) ON DELETE SET NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_user_code
  ON public."user"(code);

CREATE UNIQUE INDEX IF NOT EXISTS ux_user_email_tenant
  ON public."user"(tenant_id, lower(email));

CREATE INDEX IF NOT EXISTS ix_user_tenant_id
  ON public."user"(tenant_id);

CREATE INDEX IF NOT EXISTS ix_user_person_id
  ON public."user"(person_id);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_user_updated_at') THEN
    CREATE TRIGGER trg_user_updated_at
    BEFORE UPDATE ON public."user"
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.tenant_owner (
  tenant_id bigint PRIMARY KEY,
  user_id bigint NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_tenant_owner_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT fk_tenant_owner_user
    FOREIGN KEY (user_id) REFERENCES public."user"(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_tenant_owner_user_id
  ON public.tenant_owner(user_id);

CREATE INDEX IF NOT EXISTS ix_tenant_owner_user_id
  ON public.tenant_owner(user_id);

CREATE TABLE IF NOT EXISTS public."role" (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name varchar(60) NOT NULL,
  description varchar(255),
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_role_name
  ON public."role"(lower(name));

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_role_updated_at') THEN
    CREATE TRIGGER trg_role_updated_at
    BEFORE UPDATE ON public."role"
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.permission (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code varchar(80) NOT NULL,
  description varchar(255),
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_permission_code
  ON public.permission(lower(code));

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_permission_updated_at') THEN
    CREATE TRIGGER trg_permission_updated_at
    BEFORE UPDATE ON public.permission
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.user_role (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_user_role_user
    FOREIGN KEY (user_id) REFERENCES public."user"(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_role_role
    FOREIGN KEY (role_id) REFERENCES public."role"(id) ON DELETE CASCADE,
  CONSTRAINT uq_user_role UNIQUE (user_id, role_id)
);

CREATE INDEX IF NOT EXISTS ix_user_role_role_id
  ON public.user_role(role_id);

CREATE INDEX IF NOT EXISTS ix_user_role_user_id
  ON public.user_role(user_id);

CREATE TABLE IF NOT EXISTS public.role_permission (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  role_id bigint NOT NULL,
  permission_id bigint NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_role_permission_role
    FOREIGN KEY (role_id) REFERENCES public."role"(id) ON DELETE CASCADE,
  CONSTRAINT fk_role_permission_permission
    FOREIGN KEY (permission_id) REFERENCES public.permission(id) ON DELETE CASCADE,
  CONSTRAINT uq_role_permission UNIQUE (role_id, permission_id)
);

CREATE INDEX IF NOT EXISTS ix_role_permission_permission_id
  ON public.role_permission(permission_id);

CREATE INDEX IF NOT EXISTS ix_role_permission_role_id
  ON public.role_permission(role_id);

COMMIT;
