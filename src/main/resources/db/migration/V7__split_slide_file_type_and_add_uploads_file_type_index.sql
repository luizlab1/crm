UPDATE public.uploads
SET file_type = 'SLIDE_SAAS'
WHERE file_type = 'SLIDE';

CREATE INDEX IF NOT EXISTS ix_uploads_file_type
    ON public.uploads(file_type);
