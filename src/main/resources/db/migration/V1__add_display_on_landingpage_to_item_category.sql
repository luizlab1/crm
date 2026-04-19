ALTER TABLE IF EXISTS public.item_category
ADD COLUMN IF NOT EXISTS display_on_landingpage boolean NOT NULL DEFAULT false;
