ALTER TABLE IF EXISTS public.item_category
ADD COLUMN IF NOT EXISTS show_on_site boolean;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'item_category'
          AND column_name = 'display_on_landingpage'
    ) THEN
        UPDATE public.item_category
        SET show_on_site = COALESCE(show_on_site, display_on_landingpage, true);
    ELSE
        UPDATE public.item_category
        SET show_on_site = COALESCE(show_on_site, true);
    END IF;
END $$;

ALTER TABLE IF EXISTS public.item_category
ALTER COLUMN show_on_site SET DEFAULT true;

ALTER TABLE IF EXISTS public.item_category
ALTER COLUMN show_on_site SET NOT NULL;
