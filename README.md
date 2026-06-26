# 💰 Finwise — API de Gestão Financeira Pessoal

API REST para controle de finanças pessoais. Gerencia contas, transações, categorias, orçamentos, transferências e transações recorrentes, com dashboard analítico e importação/exportação CSV.

🔗 **Frontend em produção:** https://finwise-five-livid.vercel.app/  
📦 **Repositório do frontend:** https://github.com/ViniciuPSantos/Finwise-Front

---

## Demonstração

![LandPage do Finwise](https://github.com/user-attachments/assets/3ac15155-3f60-4642-a335-1c9b611af5cd)

![DashboardPage do Finwise](https://github.com/user-attachments/assets/630bfef7-3fdd-4865-aa6b-b4db6bde9d9b)

![ExtratoPage do Finwise](https://github.com/user-attachments/assets/6dddfd04-cca8-457d-84cb-6db901cc5cba)

![BudgetsPage do Finwise](https://github.com/user-attachments/assets/bfac2262-79aa-48b2-b895-9dc127c61068)

![AccountsPage do Finwise](https://github.com/user-attachments/assets/00ecaf7c-a2ed-4c93-85af-d43e9aee1ed6)

![CategoriesPage do Finwise](https://github.com/user-attachments/assets/677c7388-fe38-4781-a62d-e2d3d96960d7)

---

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.14 |
| Segurança | Spring Security + JWT (jjwt 0.12.6), stateless, refresh token |
| Persistência | Spring Data JPA + Flyway + MySQL 8.4 |
| Build | Maven (Maven Wrapper incluído) |
| CSV | Apache Commons CSV 1.11.0 |
| Documentação | SpringDoc OpenAPI 2.8.5 |
| Infraestrutura | Docker + Docker Compose (MySQL local) |
| Deploy | Railway (backend + banco) / Vercel (frontend) |

---

## Funcionalidades

- Autenticação com JWT (access token + refresh token) e rate limiting (5 falhas → bloqueio de 15 min)
- Cadastro cria automaticamente 8 categorias padrão (Alimentação, Transporte, Saúde, Moradia, Lazer, Educação, Salário, Outros)
- CRUD completo de contas, categorias, transações e orçamentos com **soft delete**
- Validação de saldo negativo em contas CASH, CHECKING e SAVINGS
- Transferências entre contas com reversão automática de saldo ao deletar
- Transações recorrentes (DAILY / WEEKLY / MONTHLY / YEARLY) com execução automática à meia-noite
- Filtro de transações por descrição (`?search=`), tipo, conta, categoria e período
- Exportação de transações em CSV (`GET /api/transactions/export`)
- Importação de transações via CSV (`POST /api/imports/csv`)
- Dashboard: visão geral, evolução mensal, gastos por categoria, resumo receita/despesa e comparativo mês anterior
- Swagger habilitado apenas no perfil `dev`

---

## Como rodar localmente

**Pré-requisitos:** Java 21, Docker, Git

```bash
# 1. Clone
git clone https://github.com/ViniciuPSantos/Finwise.git
cd Finwise

# 2. Configure as variáveis de ambiente
cp .env.example .env
# Edite .env com seus valores

# 3. Suba o banco de dados
docker compose up -d

# 4. Rode a aplicação (perfil dev ativa Swagger + seed de dados)
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

- API: `http://localhost:8080/api`
- Swagger: `http://localhost:8080/swagger-ui.html` (somente com perfil `dev`)

---

## Variáveis de ambiente

| Variável | Padrão | Obrigatória |
|----------|--------|------------|
| `DB_USER` | — | Sim |
| `DB_PASSWORD` | — | Sim |
| `DB_ROOT_PASSWORD` | — | Sim (Docker) |
| `JWT_SECRET` | — | Sim |
| `DB_HOST` | `localhost` | Não |
| `DB_PORT` | `3307` | Não |
| `DB_NAME` | `finwise` | Não |
| `JWT_EXPIRATION` | `3600000` (1h) | Não |
| `JWT_REFRESH_EXPIRATION` | `604800000` (7d) | Não |
| `PORT` | `8080` | Não |
| `SPRING_PROFILES_ACTIVE` | — | Não (`dev` ativa Swagger e seed) |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Não |

> **Atenção:** `SPRING_PROFILES_ACTIVE=dev` insere dados de seed e habilita o Swagger — não usar em produção.

---

## Endpoints

### Auth (público)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/auth/register` | Cadastra usuário + cria categorias padrão |
| POST | `/api/auth/login` | Autentica e retorna access + refresh token |
| POST | `/api/auth/refresh` | Renova o access token |
| POST | `/api/auth/logout` | Revoga o refresh token |

### Usuário
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/me` | Retorna dados do usuário autenticado |

### Contas
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/accounts` | Cria conta (CASH, CHECKING, SAVINGS, CREDIT_CARD, INVESTMENT) |
| GET | `/api/accounts` | Lista contas ativas |
| GET | `/api/accounts/{id}` | Detalhe da conta |
| PUT | `/api/accounts/{id}` | Atualiza conta |
| DELETE | `/api/accounts/{id}` | Soft delete |

### Categorias
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/categories` | Cria categoria |
| GET | `/api/categories` | Lista categorias ativas |
| GET | `/api/categories/{id}` | Detalhe |
| PUT | `/api/categories/{id}` | Atualiza |
| DELETE | `/api/categories/{id}` | Soft delete |

### Transações
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/transactions` | Cria transação (ajusta saldo da conta) |
| GET | `/api/transactions` | Lista com filtros e paginação (`?search=`, `?type=`, `?accountId=`, `?categoryId=`, `?startDate=`, `?endDate=`, `?page=`, `?size=`) |
| GET | `/api/transactions/{id}` | Detalhe |
| PUT | `/api/transactions/{id}` | Atualiza (reajusta saldo) |
| DELETE | `/api/transactions/{id}` | Soft delete (reverte saldo) |
| GET | `/api/transactions/export` | Exporta CSV com os mesmos filtros do list |

### Transferências
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/transfers` | Transfere entre contas do usuário |
| GET | `/api/transfers` | Lista transferências |
| GET | `/api/transfers/{id}` | Detalhe |
| DELETE | `/api/transfers/{id}` | Cancela e reverte saldos |

### Orçamentos
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/budgets` | Cria orçamento mensal por categoria |
| GET | `/api/budgets` | Lista orçamentos |
| GET | `/api/budgets/{id}` | Detalhe |
| GET | `/api/budgets/status` | Status de consumo dos orçamentos no mês atual |
| PUT | `/api/budgets/{id}` | Atualiza |
| DELETE | `/api/budgets/{id}` | Soft delete |

### Transações recorrentes
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/recurring-transactions` | Cria (DAILY / WEEKLY / MONTHLY / YEARLY) |
| GET | `/api/recurring-transactions` | Lista |
| GET | `/api/recurring-transactions/{id}` | Detalhe |
| PUT | `/api/recurring-transactions/{id}` | Atualiza |
| DELETE | `/api/recurring-transactions/{id}` | Remove |

> Execução automática diária à meia-noite via `@Scheduled`.

### Dashboard
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/dashboard/overview` | Saldo total + resumo do mês |
| GET | `/api/dashboard/income-expense-summary` | Total de receitas e despesas (`?year=&month=`) |
| GET | `/api/dashboard/spending-by-category` | Gastos agrupados por categoria |
| GET | `/api/dashboard/monthly-evolution` | Evolução mensal de receitas e despesas |
| GET | `/api/dashboard/comparison` | Comparativo mês atual vs anterior com % de variação (`?year=&month=`) |

### Importação
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/imports/csv` | Importa transações via arquivo CSV (multipart) |

---

## Testes

```bash
./mvnw test
```

Testes unitários da camada de serviço com JUnit 5 + Mockito. H2 configurado para testes de integração.

---

## Autor

**Vinícius de Paula Santos**

- GitHub: [@ViniciuPSantos](https://github.com/ViniciuPSantos)
- LinkedIn: [vinicius-de-paula-santos-dev](https://www.linkedin.com/in/vinicius-de-paula-santos-dev/)
- Portfólio: [vinicius-santos-portfolio.vercel.app](https://vinicius-santos-portfolio.vercel.app/)

---

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
