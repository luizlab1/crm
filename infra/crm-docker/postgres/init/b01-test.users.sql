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

INSERT INTO public."user" (email, password_hash, full_name)
VALUES
  ('admin@saas.com', crypt('abc123', gen_salt('bf')), 'Admin Saas'),
  ('suporte@saas.com', crypt('abc123', gen_salt('bf')), 'Suporte Saas'),
  ('owner@tenant.com', crypt('abc123', gen_salt('bf')), 'Owner Tenant'),
  ('admin@tenant.com', crypt('abc123', gen_salt('bf')), 'Admin Tenant'),
  ('suporte@tenant.com', crypt('abc123', gen_salt('bf')), 'Suporte Tenant'),
  ('seller@tenant.com', crypt('abc123', gen_salt('bf')), 'Seller Tenant'),
  ('worker@tenant.com', crypt('abc123', gen_salt('bf')), 'Worker Tenant'),
  ('seller2@tenant.com', crypt('abc123', gen_salt('bf')), 'Seller Dois'),
  ('worker2@tenant.com', crypt('abc123', gen_salt('bf')), 'Worker Dois'),
  ('worker3@tenant.com', crypt('abc123', gen_salt('bf')), 'Worker Tres');

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id
FROM public."user" u
JOIN public."role" r ON r.name = 'SAAS_ADM'
WHERE u.email = 'admin@saas.com';

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id
FROM public."user" u
JOIN public."role" r ON r.name = 'SAAS_SUPORT'
WHERE u.email = 'suporte@saas.com';

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id
FROM public."user" u
JOIN public."role" r ON r.name = 'TENANT_OWNER'
WHERE u.email = 'owner@tenant.com';

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id
FROM public."user" u
JOIN public."role" r ON r.name = 'TENANT_ADMIN'
WHERE u.email = 'admin@tenant.com';

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id
FROM public."user" u
JOIN public."role" r ON r.name = 'TENANT_SUPORT'
WHERE u.email = 'suporte@tenant.com';

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id
FROM public."user" u
JOIN public."role" r ON r.name = 'TENANT_SELLER'
WHERE u.email IN ('seller@tenant.com', 'seller2@tenant.com');

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id
FROM public."user" u
JOIN public."role" r ON r.name = 'TENANT_WORKER'
WHERE u.email IN ('worker@tenant.com', 'worker2@tenant.com', 'worker3@tenant.com');

INSERT INTO public.user_permission (user_id, permission_id)
SELECT u.id, p.id
FROM public."user" u
JOIN public.permission p ON p.code IN (
  'TENANT_VIEW',
  'TENANT_MANAGE',
  'USER_MANAGE',
  'SALES_VIEW',
  'SALES_CREATE',
  'SALES_UPDATE',
  'SALES_DELETE',
  'REPORT_VIEW'
)
WHERE u.email IN ('admin@saas.com', 'owner@tenant.com');

INSERT INTO public.user_permission (user_id, permission_id)
SELECT u.id, p.id
FROM public."user" u
JOIN public.permission p ON p.code IN (
  'TENANT_VIEW',
  'USER_MANAGE',
  'SALES_VIEW',
  'SALES_CREATE',
  'SALES_UPDATE',
  'REPORT_VIEW'
)
WHERE u.email = 'admin@tenant.com';

INSERT INTO public.user_permission (user_id, permission_id)
SELECT u.id, p.id
FROM public."user" u
JOIN public.permission p ON p.code IN (
  'TENANT_VIEW',
  'SALES_VIEW',
  'REPORT_VIEW'
)
WHERE u.email IN ('suporte@saas.com', 'suporte@tenant.com');

INSERT INTO public.user_permission (user_id, permission_id)
SELECT u.id, p.id
FROM public."user" u
JOIN public.permission p ON p.code IN (
  'SALES_VIEW',
  'SALES_CREATE',
  'SALES_UPDATE',
  'REPORT_VIEW'
)
WHERE u.email IN ('seller@tenant.com', 'seller2@tenant.com');

INSERT INTO public.user_permission (user_id, permission_id)
SELECT u.id, p.id
FROM public."user" u
JOIN public.permission p ON p.code = 'SALES_VIEW'
WHERE u.email IN ('worker@tenant.com', 'worker2@tenant.com', 'worker3@tenant.com');

COMMIT;
