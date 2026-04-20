ALTER TABLE public.uploads
    ADD COLUMN IF NOT EXISTS sort_order integer NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS ix_uploads_file_type_entity_sort_order
    ON public.uploads(file_type, entity_id, sort_order);
