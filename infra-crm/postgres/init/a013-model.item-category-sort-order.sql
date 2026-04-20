BEGIN;

ALTER TABLE public.item_category
  ADD COLUMN IF NOT EXISTS sort_order integer NOT NULL DEFAULT 0;

COMMIT;
