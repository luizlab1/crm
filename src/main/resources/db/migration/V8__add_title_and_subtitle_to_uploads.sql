ALTER TABLE public.uploads
    ADD COLUMN IF NOT EXISTS title varchar(200) NULL,
    ADD COLUMN IF NOT EXISTS subtitle varchar(300) NULL;
