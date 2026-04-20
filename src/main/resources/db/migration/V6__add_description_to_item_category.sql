ALTER TABLE IF EXISTS public.item_category
ADD COLUMN IF NOT EXISTS description varchar(500);
