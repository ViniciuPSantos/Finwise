# 💰 Finwise — API de Gestão Financeira Pessoal

Aplicação **full stack** para controle de finanças pessoais. O backend é uma **API REST em Java + Spring Boot**, com persistência em **MySQL** e ambiente containerizado em **Docker**. O frontend, em **React.js + TypeScript**, consome a API e está publicado em produção.

🔗 **Aplicação em produção:** https://finwise-five-livid.vercel.app/
📦 **Repositório do frontend:** <!-- adicione aqui o link do repositório do front, ex: https://github.com/ViniciuPSantos/Finwise-Front -->

---

## 📸 Demonstração

![LandPage do Finwise](https://github.com/user-attachments/assets/3ac15155-3f60-4642-a335-1c9b611af5cd)

![DashboardPage do Finwise](https://github.com/user-attachments/assets/630bfef7-3fdd-4865-aa6b-b4db6bde9d9b)

![ExtratoPage do Finwise](https://github.com/user-attachments/assets/6dddfd04-cca8-457d-84cb-6db901cc5cba)

![BudgetsPage do Finwise](https://github.com/user-attachments/assets/bfac2262-79aa-48b2-b895-9dc127c61068)

![AccountsPage do Finwise](https://github.com/user-attachments/assets/00ecaf7c-a2ed-4c93-85af-d43e9aee1ed6)

![CategoriesPage do Finwise](https://github.com/user-attachments/assets/677c7388-fe38-4781-a62d-e2d3d96960d7)

![ImportPage](https://github.com/user-attachments/assets/2da0304e-1936-4a57-b1dd-e0614f852b9f)

---

## 🚀 Funcionalidades

- ✅ Cadastro e autenticação de usuários
- ✅ Registro de receitas e despesas
- ✅ Categorização de transações
- ✅ Consulta de saldo e histórico de movimentações
- ✅ Edição e exclusão de lançamentos
- E muito mais!

---

## 🛠 Tecnologias

**Backend**
- Java 21
- Spring Boot
- Spring Web (API REST)
- Spring Data JPA
- Maven

**Banco de Dados**
- MySQL 8.4

**Infraestrutura / Deploy**
- Docker e Docker Compose
- Railway (backend + banco de dados)
- Vercel (frontend)

**Frontend**
- React.js
- TypeScript

---

## 📂 Estrutura do projeto

```
Finwise/
 ├── .mvn/wrapper/        # Maven Wrapper
 ├── src/                 # Código-fonte da API (Java / Spring Boot)
 ├── .env.example         # Modelo de variáveis de ambiente
 ├── docker-compose.yml   # Configuração do container MySQL
 ├── pom.xml              # Dependências e build (Maven)
 └── mvnw / mvnw.cmd      # Scripts do Maven Wrapper
```

---

## ⚙️ Como rodar localmente

### Pré-requisitos
- [Java 21](https://www.oracle.com/java/technologies/downloads/)
- [Docker](https://www.docker.com/) e Docker Compose
- [Git](https://git-scm.com/)

### 1. Clone o repositório

```bash
git clone https://github.com/ViniciuPSantos/Finwise.git
cd Finwise
```

### 2. Configure as variáveis de ambiente

Copie o arquivo de exemplo e preencha com seus valores:

```bash
cp .env.example .env
```

O arquivo `.env` deve conter:

```env
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
DB_ROOT_PASSWORD=sua_senha_root
```

### 3. Suba o banco de dados com Docker

```bash
docker compose up -d
```

Isso inicia um container MySQL 8.4 com o banco `finwise` (exposto na porta `3307` do host).

### 4. Rode a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em:

```
http://localhost:8080
```

---

## 📌 Endpoints principais

| Método | Rota | Descrição |
|--------|------|-----------|
| POST   | `/api/auth/register` | Cadastra um novo usuário |
| POST   | `/api/auth/login`    | Autentica e retorna o token |
| GET    | `/api/transactions`  | Lista as transações do usuário |
| POST   | `/api/transactions`  | Cria uma nova transação |
| PUT    | `/api/transactions/{id}` | Atualiza uma transação |
| DELETE | `/api/transactions/{id}` | Remove uma transação |

---

## 👨‍💻 Autor

**Vinícius de Paula Santos**

- GitHub: [@ViniciuPSantos](https://github.com/ViniciuPSantos)
- LinkedIn: [vinicius-de-paula-santos-dev](https://www.linkedin.com/in/vinicius-de-paula-santos-dev/)
- Portfólio: [vinicius-santos-portfolio.vercel.app](https://vinicius-santos-portfolio.vercel.app/)

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
