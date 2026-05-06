ALTER TABLE public.settings_saas_plan_benefits
  DROP COLUMN IF EXISTS subtitle,
  DROP COLUMN IF EXISTS value;
