BEGIN;

ALTER TABLE public.item_category
  ADD COLUMN IF NOT EXISTS show_on_site boolean NOT NULL DEFAULT true;

COMMIT;
