BEGIN;

DO $$
DECLARE
  v_tenant_id bigint;
  v_person_id bigint;
BEGIN
  INSERT INTO public.tenant (name, category)
  SELECT 'SAAS COMPANY', 'SAAS'
  WHERE NOT EXISTS (
    SELECT 1 FROM public.tenant WHERE name = 'SAAS COMPANY'
  );

  SELECT id INTO v_tenant_id
  FROM public.tenant
  WHERE name = 'SAAS COMPANY';

  SELECT pl.person_id INTO v_person_id
  FROM public.person_legal pl
  JOIN public.person p ON p.id = pl.person_id
  WHERE p.tenant_id = v_tenant_id
    AND pl.corporate_name = 'SAAS COMPANY'
  LIMIT 1;

  IF v_person_id IS NULL THEN
    INSERT INTO public.person (tenant_id)
    VALUES (v_tenant_id)
    RETURNING id INTO v_person_id;

    INSERT INTO public.person_legal (person_id, corporate_name, trade_name, cnpj)
    VALUES (
      v_person_id,
      'SAAS COMPANY',
      'SAAS COMPANY',
      lpad(v_person_id::text, 14, '0')
    );
  END IF;

  INSERT INTO public.tenant_owner (tenant_id, user_id)
  SELECT v_tenant_id, u.id
  FROM public."user" u
  WHERE u.tenant_id = v_tenant_id
    AND lower(u.email) = 'owner@saas.com'
  ON CONFLICT DO NOTHING;
END $$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM public."role" LIMIT 1) THEN
    RETURN;
  END IF;
END $$;

INSERT INTO public."role" (name, description) VALUES
('SAAS_ADM','Administrador global da plataforma SaaS com acesso total.'),
('SAAS_SUPORT','Suporte técnico da plataforma SaaS.'),
('TENANT_OWNER','Proprietário do tenant com controle total do ambiente.'),
('TENANT_ADMIN','Administrador do tenant responsável por gestão e configuração.'),
('TENANT_SUPORT','Suporte operacional do tenant.'),
('TENANT_SELLER','Vendedor do tenant responsável por negociações e pedidos.'),
('TENANT_WORKER','Operador do tenant com acesso operacional básico.');

INSERT INTO public.permission (code, description) VALUES
('TENANT_VIEW','Permite visualizar dados do tenant.'),
('TENANT_MANAGE','Permite gerenciar configurações do tenant.'),
('USER_MANAGE','Permite gerenciar usuários do tenant.'),
('SALES_VIEW','Permite visualizar informações comerciais.'),
('SALES_CREATE','Permite criar oportunidades e pedidos.'),
('SALES_UPDATE','Permite atualizar negociações existentes.'),
('SALES_DELETE','Permite remover registros comerciais.'),
('REPORT_VIEW','Permite acesso a relatórios do sistema.');

CREATE OR REPLACE FUNCTION public._seed_create_user(
  p_tenant_id bigint,
  p_email text,
  p_full_name text,
  p_role_name text
) RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  v_person_id bigint;
  v_user_id   bigint;
BEGIN
  SELECT u.id INTO v_user_id
  FROM public."user" u
  WHERE u.tenant_id = p_tenant_id AND lower(u.email) = lower(p_email);

  IF v_user_id IS NULL THEN
    INSERT INTO public.person (tenant_id)
    VALUES (p_tenant_id)
    RETURNING id INTO v_person_id;

    INSERT INTO public.person_physical (person_id, full_name, cpf, birth_date)
    VALUES (v_person_id, p_full_name, lpad(v_person_id::text, 11, '0'), NULL);

    INSERT INTO public."user" (tenant_id, person_id, email, password_hash)
    VALUES (p_tenant_id, v_person_id, p_email, crypt('abc123', gen_salt('bf')))
    RETURNING id INTO v_user_id;
  END IF;

  INSERT INTO public.user_role (user_id, role_id)
  SELECT v_user_id, r.id
  FROM public."role" r
  WHERE r.name = p_role_name
  ON CONFLICT DO NOTHING;
END;
$$;

DO $$
DECLARE
  saas_id bigint := (SELECT id FROM public.tenant WHERE name = 'SAAS COMPANY');
BEGIN
  PERFORM public._seed_create_user(saas_id, 'admin@saas.com',   'Admin Saas',   'SAAS_ADM');
  PERFORM public._seed_create_user(saas_id, 'suporte@saas.com', 'Suporte Saas', 'SAAS_SUPORT');
  PERFORM public._seed_create_user(saas_id, 'owner@saas.com',   'Saas Owner',   'SAAS_ADM');
  PERFORM public._seed_create_user(saas_id, 'owner@tenant.com',   'Owner Tenant',   'TENANT_OWNER');
  PERFORM public._seed_create_user(saas_id, 'admin@tenant.com',   'Admin Tenant',   'TENANT_ADMIN');
  PERFORM public._seed_create_user(saas_id, 'suporte@tenant.com', 'Suporte Tenant', 'TENANT_SUPORT');
  PERFORM public._seed_create_user(saas_id, 'seller@tenant.com',  'Seller Tenant',  'TENANT_SELLER');
  PERFORM public._seed_create_user(saas_id, 'worker@tenant.com',  'Worker Tenant',  'TENANT_WORKER');
END $$;

DROP FUNCTION public._seed_create_user(bigint, text, text, text);

INSERT INTO public.role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM public."role" r
JOIN public.permission p ON (
  (r.name IN ('SAAS_ADM','TENANT_OWNER') AND p.code IN ('TENANT_VIEW','TENANT_MANAGE','USER_MANAGE','SALES_VIEW','SALES_CREATE','SALES_UPDATE','SALES_DELETE','REPORT_VIEW')) OR
  (r.name = 'TENANT_ADMIN' AND p.code IN ('TENANT_VIEW','USER_MANAGE','SALES_VIEW','SALES_CREATE','SALES_UPDATE','REPORT_VIEW')) OR
  (r.name IN ('SAAS_SUPORT','TENANT_SUPORT') AND p.code IN ('TENANT_VIEW','SALES_VIEW','REPORT_VIEW')) OR
  (r.name = 'TENANT_SELLER' AND p.code IN ('SALES_VIEW','SALES_CREATE','SALES_UPDATE','REPORT_VIEW')) OR
  (r.name = 'TENANT_WORKER' AND p.code = 'SALES_VIEW')
)
ON CONFLICT DO NOTHING;

COMMIT;
