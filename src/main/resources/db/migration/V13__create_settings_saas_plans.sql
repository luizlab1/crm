CREATE TABLE IF NOT EXISTS public.settings_saas_plan (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tenant_id   bigint       NOT NULL,
  name        varchar(255) NOT NULL,
  description text,
  category    varchar(64)  NOT NULL,
  created_at  timestamptz  NOT NULL DEFAULT now(),
  updated_at  timestamptz  NOT NULL DEFAULT now(),
  CONSTRAINT fk_settings_saas_plan_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
  CONSTRAINT ck_settings_saas_plan_category
    CHECK (category IN ('PROFESSIONAL_AUTONOMOUS', 'BUSINESS'))
);

CREATE INDEX IF NOT EXISTS ix_settings_saas_plan_tenant_id
  ON public.settings_saas_plan(tenant_id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_settings_saas_plan_tenant_lower_name
  ON public.settings_saas_plan(tenant_id, lower(name));

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_settings_saas_plan_updated_at') THEN
    CREATE TRIGGER trg_settings_saas_plan_updated_at
    BEFORE UPDATE ON public.settings_saas_plan
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.settings_saas_plan_benefits (
  id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  plan_id      bigint      NOT NULL,
  description  text        NOT NULL,
  created_at   timestamptz NOT NULL DEFAULT now(),
  updated_at   timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT fk_settings_saas_plan_benefits_plan
    FOREIGN KEY (plan_id) REFERENCES public.settings_saas_plan(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS ix_settings_saas_plan_benefits_plan_id
  ON public.settings_saas_plan_benefits(plan_id);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_settings_saas_plan_benefits_updated_at') THEN
    CREATE TRIGGER trg_settings_saas_plan_benefits_updated_at
    BEFORE UPDATE ON public.settings_saas_plan_benefits
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

DO $$
DECLARE
  v_plan_id bigint;
BEGIN
  IF EXISTS (SELECT 1 FROM public.tenant WHERE id = 1) THEN
    INSERT INTO public.settings_saas_plan (tenant_id, name, description, category)
    SELECT 1, 'Essencial', 'Plano para autonomos', 'PROFESSIONAL_AUTONOMOUS'
    WHERE NOT EXISTS (
      SELECT 1
      FROM public.settings_saas_plan p
      WHERE p.tenant_id = 1
        AND lower(p.name) = lower('Essencial')
    )
    RETURNING id INTO v_plan_id;

    IF v_plan_id IS NULL THEN
      SELECT p.id INTO v_plan_id
      FROM public.settings_saas_plan p
      WHERE p.tenant_id = 1
        AND lower(p.name) = lower('Essencial')
      LIMIT 1;
    END IF;

    IF v_plan_id IS NOT NULL THEN
      INSERT INTO public.settings_saas_plan_benefits (plan_id, description)
      SELECT v_plan_id, x.description
      FROM (VALUES ('Atendimento prioritario'), ('Relatorios mensais')) AS x(description)
      WHERE NOT EXISTS (
        SELECT 1
        FROM public.settings_saas_plan_benefits b
        WHERE b.plan_id = v_plan_id
          AND lower(b.description) = lower(x.description)
      );
    END IF;
  END IF;
END $$;
