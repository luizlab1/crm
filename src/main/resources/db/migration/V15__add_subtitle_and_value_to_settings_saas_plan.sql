ALTER TABLE public.settings_saas_plan
  ADD COLUMN IF NOT EXISTS subtitle text,
  ADD COLUMN IF NOT EXISTS value text;

UPDATE public.settings_saas_plan
SET subtitle = COALESCE(NULLIF(trim(subtitle), ''), name),
    value = COALESCE(NULLIF(trim(value), ''), description, name)
WHERE subtitle IS NULL
   OR trim(subtitle) = ''
   OR value IS NULL
   OR trim(value) = '';

ALTER TABLE public.settings_saas_plan
  ALTER COLUMN subtitle SET NOT NULL,
  ALTER COLUMN value SET NOT NULL;
