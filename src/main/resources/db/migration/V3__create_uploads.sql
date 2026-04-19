CREATE TABLE IF NOT EXISTS public.uploads (
    id           uuid          PRIMARY KEY,
    file_type    varchar(60)   NOT NULL,
    entity_id    bigint        NOT NULL,
    tenant_id    bigint        NOT NULL,
    item_id      bigint        NULL,
    category_id  bigint        NULL,
    customer_id  bigint        NULL,
    worker_id    bigint        NULL,
    file_name    varchar(255)  NOT NULL,
    file_path    varchar(1000) NOT NULL,
    content_type varchar(150),
    size         bigint,
    width        integer       NULL,
    height       integer       NULL,
    legend       varchar(500)  NULL,
    created_at   timestamptz   NOT NULL DEFAULT now(),
    CONSTRAINT fk_uploads_item
        FOREIGN KEY (item_id) REFERENCES public.item(id) ON DELETE SET NULL,
    CONSTRAINT fk_uploads_category
        FOREIGN KEY (category_id) REFERENCES public.item_category(id) ON DELETE SET NULL,
    CONSTRAINT fk_uploads_tenant
        FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE,
    CONSTRAINT fk_uploads_customer
        FOREIGN KEY (customer_id) REFERENCES public.customer(id) ON DELETE SET NULL,
    CONSTRAINT fk_uploads_worker
        FOREIGN KEY (worker_id) REFERENCES public.worker(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS ix_uploads_file_type_entity_id ON public.uploads(file_type, entity_id);
CREATE INDEX IF NOT EXISTS ix_uploads_item_id             ON public.uploads(item_id);
CREATE INDEX IF NOT EXISTS ix_uploads_category_id         ON public.uploads(category_id);
CREATE INDEX IF NOT EXISTS ix_uploads_tenant_id           ON public.uploads(tenant_id);
CREATE INDEX IF NOT EXISTS ix_uploads_customer_id         ON public.uploads(customer_id);
CREATE INDEX IF NOT EXISTS ix_uploads_worker_id           ON public.uploads(worker_id);
CREATE INDEX IF NOT EXISTS ix_uploads_created_at          ON public.uploads(created_at);
