ALTER TABLE IF EXISTS public.item_category
    ADD COLUMN IF NOT EXISTS sort_order integer NOT NULL DEFAULT 0;
