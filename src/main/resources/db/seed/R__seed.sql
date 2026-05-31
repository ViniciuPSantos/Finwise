-- ========================================================================
-- SEED DE DESENVOLVIMENTO (dev only) — repeatable + idempotente
-- Senha dos dois usuários: senha123 (hash BCrypt, throwaway, apenas dev)
-- NÃO roda em produção (locations só inclui db/seed no perfil dev).
-- ========================================================================

INSERT INTO users (created_at, email, name, password)
SELECT NOW(6), 'vinicius@finwise.dev', 'Vinicius', '$2a$10$2tQuXuL5//YZ7A3Jr1NDy.zgOkudODHWr9/T3M8.KLlpa8shfGlXO'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'vinicius@finwise.dev');

INSERT INTO users (created_at, email, name, password)
SELECT NOW(6), 'maria@finwise.dev', 'Maria', '$2a$10$2tQuXuL5//YZ7A3Jr1NDy.zgOkudODHWr9/T3M8.KLlpa8shfGlXO'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'maria@finwise.dev');

-- contas do Vinicius (resolve user_id pelo email)
INSERT INTO accounts (created_at, balance, name, type, user_id)
SELECT NOW(6), 1000.00, 'Nubank', 'CHECKING', u.id FROM users u WHERE u.email = 'vinicius@finwise.dev'
  AND NOT EXISTS (SELECT 1 FROM accounts a WHERE a.user_id = u.id AND a.name = 'Nubank');
INSERT INTO accounts (created_at, balance, name, type, user_id)
SELECT NOW(6), 500.00, 'Carteira', 'CASH', u.id FROM users u WHERE u.email = 'vinicius@finwise.dev'
  AND NOT EXISTS (SELECT 1 FROM accounts a WHERE a.user_id = u.id AND a.name = 'Carteira');

-- contas da Maria
INSERT INTO accounts (created_at, balance, name, type, user_id)
SELECT NOW(6), 2000.00, 'Itau', 'CHECKING', u.id FROM users u WHERE u.email = 'maria@finwise.dev'
  AND NOT EXISTS (SELECT 1 FROM accounts a WHERE a.user_id = u.id AND a.name = 'Itau');
INSERT INTO accounts (created_at, balance, name, type, user_id)
SELECT NOW(6), 0.00, 'Cartao', 'CREDIT_CARD', u.id FROM users u WHERE u.email = 'maria@finwise.dev'
  AND NOT EXISTS (SELECT 1 FROM accounts a WHERE a.user_id = u.id AND a.name = 'Cartao');

-- categorias do Vinicius
INSERT INTO categories (created_at, name, user_id)
SELECT NOW(6), c.nome, u.id FROM users u
  JOIN (SELECT 'Alimentação' AS nome UNION ALL SELECT 'Transporte' UNION ALL SELECT 'Lazer' UNION ALL SELECT 'Salário') c
  WHERE u.email = 'vinicius@finwise.dev'
  AND NOT EXISTS (SELECT 1 FROM categories x WHERE x.user_id = u.id AND x.name = c.nome);

-- categorias da Maria
INSERT INTO categories (created_at, name, user_id)
SELECT NOW(6), c.nome, u.id FROM users u
  JOIN (SELECT 'Mercado' AS nome UNION ALL SELECT 'Saúde' UNION ALL SELECT 'Educação') c
  WHERE u.email = 'maria@finwise.dev'
  AND NOT EXISTS (SELECT 1 FROM categories x WHERE x.user_id = u.id AND x.name = c.nome);