BEGIN;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM public."role" LIMIT 1) THEN
    RETURN;
  END IF;
END $$;

INSERT INTO public."role" (name, description)
VALUES
  ('SAAS_ADM', 'Administrador global da plataforma SaaS com acesso total.'),
  ('SAAS_SUPORT', 'Suporte técnico da plataforma SaaS.'),
  ('TENANT_OWNER', 'Proprietário do tenant com controle total do ambiente.'),
  ('TENANT_ADMIN', 'Administrador do tenant responsável por gestão e configuração.'),
  ('TENANT_SUPORT', 'Suporte operacional do tenant.'),
  ('TENANT_SELLER', 'Vendedor do tenant responsável por negociações e pedidos.'),
  ('TENANT_WORKER', 'Operador do tenant com acesso operacional básico.');

INSERT INTO public.permission (code, description)
VALUES
  ('TENANT_VIEW', 'Permite visualizar dados do tenant.'),
  ('TENANT_MANAGE', 'Permite gerenciar configurações do tenant.'),
  ('USER_MANAGE', 'Permite gerenciar usuários do tenant.'),
  ('SALES_VIEW', 'Permite visualizar informações comerciais.'),
  ('SALES_CREATE', 'Permite criar oportunidades e pedidos.'),
  ('SALES_UPDATE', 'Permite atualizar negociações existentes.'),
  ('SALES_DELETE', 'Permite remover registros comerciais.'),
  ('REPORT_VIEW', 'Permite acesso a relatórios do sistema.');

WITH
saas AS (
  INSERT INTO public.tenant (name, category)
  SELECT 'SAAS', 'SAAS'
  WHERE NOT EXISTS (SELECT 1 FROM public.tenant WHERE name = 'SAAS')
  RETURNING id
),
tenant_app AS (
  INSERT INTO public.tenant (name, category)
  SELECT 'TENANT', 'DEFAULT'
  WHERE NOT EXISTS (SELECT 1 FROM public.tenant WHERE name = 'TENANT')
  RETURNING id
),
t AS (
  SELECT
    (SELECT id FROM public.tenant WHERE name = 'SAAS')   AS saas_id,
    (SELECT id FROM public.tenant WHERE name = 'TENANT') AS tenant_id
)
INSERT INTO public."user" (tenant_id, email, password_hash, full_name)
SELECT x.tenant_id, x.email, x.password_hash, x.full_name
FROM (
  SELECT t.saas_id   AS tenant_id, 'admin@saas.com'   AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Admin Saas'   AS full_name FROM t
  UNION ALL
  SELECT t.saas_id   AS tenant_id, 'suporte@saas.com' AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Suporte Saas' AS full_name FROM t
  UNION ALL
  SELECT t.tenant_id AS tenant_id, 'owner@tenant.com' AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Owner Tenant' AS full_name FROM t
  UNION ALL
  SELECT t.tenant_id AS tenant_id, 'admin@tenant.com' AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Admin Tenant' AS full_name FROM t
  UNION ALL
  SELECT t.tenant_id AS tenant_id, 'suporte@tenant.com' AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Suporte Tenant' AS full_name FROM t
  UNION ALL
  SELECT t.tenant_id AS tenant_id, 'seller@tenant.com' AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Seller Tenant' AS full_name FROM t
  UNION ALL
  SELECT t.tenant_id AS tenant_id, 'worker@tenant.com' AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Worker Tenant' AS full_name FROM t
  UNION ALL
  SELECT t.tenant_id AS tenant_id, 'seller2@tenant.com' AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Seller Dois' AS full_name FROM t
  UNION ALL
  SELECT t.tenant_id AS tenant_id, 'worker2@tenant.com' AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Worker Dois' AS full_name FROM t
  UNION ALL
  SELECT t.tenant_id AS tenant_id, 'worker3@tenant.com' AS email, crypt('abc123', gen_salt('bf')) AS password_hash, 'Worker Tres' AS full_name FROM t
) x
WHERE NOT EXISTS (
  SELECT 1
  FROM public."user" u
  WHERE u.tenant_id = x.tenant_id AND lower(u.email) = lower(x.email)
);

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id FROM public."user" u JOIN public."role" r ON r.name = 'SAAS_ADM'
WHERE u.email = 'admin@saas.com' ON CONFLICT DO NOTHING;

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id FROM public."user" u JOIN public."role" r ON r.name = 'SAAS_SUPORT'
WHERE u.email = 'suporte@saas.com' ON CONFLICT DO NOTHING;

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id FROM public."user" u JOIN public."role" r ON r.name = 'TENANT_OWNER'
WHERE u.email = 'owner@tenant.com' ON CONFLICT DO NOTHING;

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id FROM public."user" u JOIN public."role" r ON r.name = 'TENANT_ADMIN'
WHERE u.email = 'admin@tenant.com' ON CONFLICT DO NOTHING;

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id FROM public."user" u JOIN public."role" r ON r.name = 'TENANT_SUPORT'
WHERE u.email = 'suporte@tenant.com' ON CONFLICT DO NOTHING;

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id FROM public."user" u JOIN public."role" r ON r.name = 'TENANT_SELLER'
WHERE u.email IN ('seller@tenant.com','seller2@tenant.com') ON CONFLICT DO NOTHING;

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id FROM public."user" u JOIN public."role" r ON r.name = 'TENANT_WORKER'
WHERE u.email IN ('worker@tenant.com','worker2@tenant.com','worker3@tenant.com') ON CONFLICT DO NOTHING;

INSERT INTO public.role_permission (role_id, permission_id)
SELECT r.id, p.id FROM public."role" r JOIN public.permission p
ON p.code IN ('TENANT_VIEW','TENANT_MANAGE','USER_MANAGE','SALES_VIEW','SALES_CREATE','SALES_UPDATE','SALES_DELETE','REPORT_VIEW')
WHERE r.name = 'SAAS_ADM' ON CONFLICT DO NOTHING;

INSERT INTO public.role_permission (role_id, permission_id)
SELECT r.id, p.id FROM public."role" r JOIN public.permission p
ON p.code IN ('TENANT_VIEW','TENANT_MANAGE','USER_MANAGE','SALES_VIEW','SALES_CREATE','SALES_UPDATE','SALES_DELETE','REPORT_VIEW')
WHERE r.name = 'TENANT_OWNER' ON CONFLICT DO NOTHING;

INSERT INTO public.role_permission (role_id, permission_id)
SELECT r.id, p.id FROM public."role" r JOIN public.permission p
ON p.code IN ('TENANT_VIEW','USER_MANAGE','SALES_VIEW','SALES_CREATE','SALES_UPDATE','REPORT_VIEW')
WHERE r.name = 'TENANT_ADMIN' ON CONFLICT DO NOTHING;

INSERT INTO public.role_permission (role_id, permission_id)
SELECT r.id, p.id FROM public."role" r JOIN public.permission p
ON p.code IN ('TENANT_VIEW','SALES_VIEW','REPORT_VIEW')
WHERE r.name IN ('SAAS_SUPORT','TENANT_SUPORT') ON CONFLICT DO NOTHING;

INSERT INTO public.role_permission (role_id, permission_id)
SELECT r.id, p.id FROM public."role" r JOIN public.permission p
ON p.code IN ('SALES_VIEW','SALES_CREATE','SALES_UPDATE','REPORT_VIEW')
WHERE r.name = 'TENANT_SELLER' ON CONFLICT DO NOTHING;

INSERT INTO public.role_permission (role_id, permission_id)
SELECT r.id, p.id FROM public."role" r JOIN public.permission p
ON p.code = 'SALES_VIEW'
WHERE r.name = 'TENANT_WORKER' ON CONFLICT DO NOTHING;

COMMIT;
