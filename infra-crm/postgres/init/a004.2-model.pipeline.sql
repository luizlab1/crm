BEGIN;

CREATE TABLE IF NOT EXISTS public.pipeline_flow (
  id           bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tenant_id    bigint        NOT NULL,
  code         varchar(60)   NOT NULL,
  name         varchar(150)  NOT NULL,
  description  varchar(255),
  is_active    boolean       NOT NULL DEFAULT true,
  created_at   timestamptz   NOT NULL DEFAULT now(),
  updated_at   timestamptz   NOT NULL DEFAULT now(),
  CONSTRAINT fk_pipeline_flow_tenant
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pipeline_flow_tenant_lower_code
  ON public.pipeline_flow (tenant_id, lower(code));

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_pipeline_flow_updated_at') THEN
    CREATE TRIGGER trg_pipeline_flow_updated_at
    BEFORE UPDATE ON public.pipeline_flow
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.pipeline_flow_step (
  id               bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pipeline_flow_id bigint        NOT NULL,
  step_order       integer       NOT NULL,
  code             varchar(60)   NOT NULL,
  name             varchar(150)  NOT NULL,
  description      varchar(255),
  step_type        varchar(60)   NOT NULL,
  is_terminal      boolean       NOT NULL DEFAULT false,
  created_at       timestamptz   NOT NULL DEFAULT now(),
  updated_at       timestamptz   NOT NULL DEFAULT now(),
  CONSTRAINT fk_pipeline_flow_step_flow
    FOREIGN KEY (pipeline_flow_id) REFERENCES public.pipeline_flow(id) ON DELETE CASCADE,
  CONSTRAINT ck_pipeline_flow_step_order_positive
    CHECK (step_order > 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pipeline_flow_step_flow_order
  ON public.pipeline_flow_step (pipeline_flow_id, step_order);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pipeline_flow_step_flow_lower_code
  ON public.pipeline_flow_step (pipeline_flow_id, lower(code));

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_pipeline_flow_step_updated_at') THEN
    CREATE TRIGGER trg_pipeline_flow_step_updated_at
    BEFORE UPDATE ON public.pipeline_flow_step
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

-- =========================
-- EXEMPLO: FLUXO "VENDA DELIVERY"
-- =========================

WITH t AS (
  SELECT id AS tenant_id
  FROM public.tenant
  ORDER BY id
  LIMIT 1
),
f AS (
  INSERT INTO public.pipeline_flow (tenant_id, code, name, description)
  SELECT
    t.tenant_id,
    'VENDA_DELIVERY',
    'Venda Delivery',
    'Fluxo padrão de venda delivery: lead WhatsApp até entrega'
  FROM t
  WHERE NOT EXISTS (
    SELECT 1
    FROM public.pipeline_flow cf
    WHERE cf.tenant_id = t.tenant_id
      AND lower(cf.code) = 'venda_delivery'
  )
  RETURNING id
)
INSERT INTO public.pipeline_flow_step
  (pipeline_flow_id, step_order, code, name, description, step_type, is_terminal)
SELECT * FROM (
  VALUES
    ((SELECT id FROM f), 1, 'LEAD_WHATSAPP',     'Lead WhatsApp',      'Entrada do lead via WhatsApp',                     'LEAD',        false),
    ((SELECT id FROM f), 2, 'QUALIFICACAO',      'Qualificação',       'Qualificação do lead e intenção de compra',       'FUNNEL',      false),
    ((SELECT id FROM f), 3, 'PEDIDO_CRIADO',     'Pedido Criado',      'Pedido criado no sistema',                        'ORDER',       false),
    ((SELECT id FROM f), 4, 'PAGAMENTO',         'Pagamento',          'Confirmação de pagamento',                        'PAYMENT',     false),
    ((SELECT id FROM f), 5, 'PREPARACAO',        'Preparação',         'Pedido em preparação',                            'FULFILLMENT', false),
    ((SELECT id FROM f), 6, 'SAIU_PARA_ENTREGA', 'Saiu para Entrega',  'Pedido saiu para entrega',                        'DELIVERY',    false),
    ((SELECT id FROM f), 7, 'ENTREGUE',          'Entregue',           'Pedido entregue ao cliente',                      'DELIVERY',    true)
) s(pipeline_flow_id, step_order, code, name, description, step_type, is_terminal)
WHERE s.pipeline_flow_id IS NOT NULL
ON CONFLICT DO NOTHING;

COMMIT;