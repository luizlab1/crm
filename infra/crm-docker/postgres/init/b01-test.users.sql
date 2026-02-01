BEGIN;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM public."role" LIMIT 1) THEN
    RETURN;
  END IF;
END $$;

INSERT INTO public."role" (name, description)
VALUES
  ('ADMIN', 'Administrador do sistema de força de venda com acesso total.'),
  ('SALES_MANAGER', 'Gestor comercial responsável por equipes e metas.'),
  ('SALES_REP', 'Representante de vendas responsável por negociações.'),
  ('SUPPORT', 'Suporte operacional ao time comercial.');

INSERT INTO public.grantt (code, description)
VALUES
  ('SALES_VIEW', 'Permite visualizar informações comerciais.'),
  ('SALES_CREATE', 'Permite criar oportunidades e pedidos.'),
  ('SALES_UPDATE', 'Permite atualizar negociações existentes.'),
  ('SALES_DELETE', 'Permite remover registros comerciais.'),
  ('REPORT_VIEW', 'Permite acesso a relatórios de vendas.'),
  ('USER_MANAGE', 'Permite gerenciar usuários do sistema.');

INSERT INTO public."user" (email, password_hash, full_name)
VALUES
  ('joao.silva@crm.com', crypt('abc123', gen_salt('bf')), 'Joao Silva'),
  ('maria.souza@crm.com', crypt('abc123', gen_salt('bf')), 'Maria Souza'),
  ('carlos.pereira@crm.com', crypt('abc123', gen_salt('bf')), 'Carlos Pereira'),
  ('ana.costa@crm.com', crypt('abc123', gen_salt('bf')), 'Ana Costa'),
  ('paulo.lima@crm.com', crypt('abc123', gen_salt('bf')), 'Paulo Lima'),
  ('fernanda.rocha@crm.com', crypt('abc123', gen_salt('bf')), 'Fernanda Rocha'),
  ('lucas.mendes@crm.com', crypt('abc123', gen_salt('bf')), 'Lucas Mendes'),
  ('juliana.alves@crm.com', crypt('abc123', gen_salt('bf')), 'Juliana Alves'),
  ('rafael.ribeiro@crm.com', crypt('abc123', gen_salt('bf')), 'Rafael Ribeiro'),
  ('beatriz.nogueira@crm.com', crypt('abc123', gen_salt('bf')), 'Beatriz Nogueira');

INSERT INTO public.user_role (user_id, role_id)
SELECT u.id, r.id
FROM public."user" u
JOIN public."role" r ON r.name IN ('ADMIN', 'SALES_MANAGER', 'SALES_REP')
WHERE u.email IN (
  'joao.silva@crm.com',
  'maria.souza@crm.com',
  'carlos.pereira@crm.com',
  'ana.costa@crm.com',
  'paulo.lima@crm.com',
  'fernanda.rocha@crm.com',
  'lucas.mendes@crm.com',
  'juliana.alves@crm.com',
  'rafael.ribeiro@crm.com',
  'beatriz.nogueira@crm.com'
);

INSERT INTO public.user_grant (user_id, grantt_id)
SELECT u.id, g.id
FROM public."user" u
JOIN public.grantt g ON g.code IN (
  'SALES_VIEW',
  'SALES_CREATE',
  'SALES_UPDATE',
  'REPORT_VIEW'
);

COMMIT;
