BEGIN;

ALTER TABLE public.item_tag
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_item_tag_updated_at') THEN
    CREATE TRIGGER trg_item_tag_updated_at
    BEFORE UPDATE ON public.item_tag
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

COMMIT;
