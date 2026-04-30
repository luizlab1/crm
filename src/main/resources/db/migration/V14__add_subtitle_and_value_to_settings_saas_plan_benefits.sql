ALTER TABLE public.settings_saas_plan_benefits
  ADD COLUMN IF NOT EXISTS subtitle text,
  ADD COLUMN IF NOT EXISTS value text;

UPDATE public.settings_saas_plan_benefits
SET subtitle = COALESCE(NULLIF(trim(subtitle), ''), description),
    value = COALESCE(NULLIF(trim(value), ''), description)
WHERE subtitle IS NULL
   OR trim(subtitle) = ''
   OR value IS NULL
   OR trim(value) = '';

ALTER TABLE public.settings_saas_plan_benefits
  ALTER COLUMN subtitle SET NOT NULL,
  ALTER COLUMN value SET NOT NULL;
