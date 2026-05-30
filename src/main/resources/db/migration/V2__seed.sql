-- ========================================================================
-- SEED DE DESENVOLVIMENTO (dev only)
-- Senha dos dois usuários: senha123  (hash BCrypt, throwaway, apenas dev)
-- NÃO usar este seed em produção.
-- ========================================================================

INSERT INTO users (created_at, email, name, password) VALUES
  (NOW(6), 'vinicius@finwise.dev', 'Vinicius', '$2a$10$2tQuXuL5//YZ7A3Jr1NDy.zgOkudODHWr9/T3M8.KLlpa8shfGlXO'),
  (NOW(6), 'maria@finwise.dev',    'Maria',    '$2a$10$2tQuXuL5//YZ7A3Jr1NDy.zgOkudODHWr9/T3M8.KLlpa8shfGlXO');

INSERT INTO accounts (created_at, balance, name, type, user_id) VALUES
  (NOW(6), 1000.00, 'Nubank',   'CHECKING',    1),
  (NOW(6),  500.00, 'Carteira', 'CASH',        1),
  (NOW(6), 2000.00, 'Itau',     'CHECKING',    2),
  (NOW(6),    0.00, 'Cartao',   'CREDIT_CARD', 2);

INSERT INTO categories (created_at, name, user_id) VALUES
  (NOW(6), 'Alimentação', 1),
  (NOW(6), 'Transporte',  1),
  (NOW(6), 'Lazer',       1),
  (NOW(6), 'Salário',     1);

INSERT INTO categories (created_at, name, user_id) VALUES
  (NOW(6), 'Mercado',   2),
  (NOW(6), 'Saúde',     2),
  (NOW(6), 'Educação',  2);