ALTER TABLE public.settings_saas_plan
  ADD COLUMN IF NOT EXISTS subtitle text,
  ADD COLUMN IF NOT EXISTS value text;

UPDATE public.settings_saas_plan p
SET subtitle = COALESCE(NULLIF(trim(p.subtitle), ''), b.subtitle, b.description, p.name),
    value = COALESCE(NULLIF(trim(p.value), ''), b.value, b.description, p.name)
FROM LATERAL (
  SELECT sb.subtitle, sb.value, sb.description
  FROM public.settings_saas_plan_benefits sb
  WHERE sb.plan_id = p.id
  ORDER BY sb.id
  LIMIT 1
) b
WHERE p.subtitle IS NULL
   OR trim(p.subtitle) = ''
   OR p.value IS NULL
   OR trim(p.value) = '';

UPDATE public.settings_saas_plan p
SET subtitle = COALESCE(NULLIF(trim(p.subtitle), ''), p.name),
    value = COALESCE(NULLIF(trim(p.value), ''), p.name)
WHERE p.subtitle IS NULL
   OR trim(p.subtitle) = ''
   OR p.value IS NULL
   OR trim(p.value) = '';

ALTER TABLE public.settings_saas_plan
  ALTER COLUMN subtitle SET NOT NULL,
  ALTER COLUMN value SET NOT NULL;
