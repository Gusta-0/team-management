

# 🎯 **Team Management API**

**Sistema completo de gerenciamento de equipes, tarefas, projetos e análises**, desenvolvido em **Java + Spring Boot**, com autenticação via **JWT**, documentação automática via **Swagger**, versionamento do banco com **Flyway**, testes com **JUnit/Mockito**, conteinerização com **Docker**, e deploy no **Render**.
Esta API fornecerá dados para um **frontend já existente**.

---

<br>

# 🚀 **Sumário**

* [📌 Visão Geral](#-visão-geral)
* [✨ Principais Funcionalidades](#-principais-funcionalidades)
* [🛠 Tecnologias Utilizadas](#-tecnologias-utilizadas)
* [📂 Estrutura do Projeto](#-estrutura-do-projeto)
* [⚙️ Configuração do Ambiente](#️-configuração-do-ambiente)

    * [.env necessário](#env-necessário)
* [🐳 Como Rodar com Docker](#-como-rodar-com-docker)
* [▶️ Rodando localmente sem Docker](#️-rodando-localmente-sem-docker)
* [🔐 Autenticação & Segurança](#-autenticação--segurança)
* [📚 Documentação (Swagger)](#-documentação-swagger)
* [🧩 Endpoints Principais](#-endpoints-principais)
* [🧪 Testes Automatizados](#-testes-automatizados)
* [🗃 Versionamento de Banco — Flyway](#-versionamento-de-banco--flyway)
* [☁️ Deploy — Render](#️-deploy--render)
* [📈 Futuras Melhorias](#-futuras-melhorias)

---

<br>

# 📌 **Visão Geral**

A **Team Management API** é uma aplicação backend robusta e escalável destinada ao gerenciamento completo de:

* Membros da equipe
* Tarefas
* Projetos
* Indicadores analíticos e dashboard
* Autenticação e controle de acesso
* Recuperação de senha

Ela segue boas práticas do Spring Boot, princípios REST, camadas organizadas (Controller → Service → Repository), tratamento global de erros, testes e documentação completa.

---

<br>

# ✨ **Principais Funcionalidades**

### 👥 **Gestão de Membros**

* Criar, atualizar, listar e excluir membros
* Filtrar por nome, função e departamento
* Indicadores de produtividade (Analytics)

### 📝 **Gestão de Tarefas**

* CRUD completo
* Registro de prioridade, status, responsável e prazo
* Cálculo de progresso e indicadores
* Tendências de criação/conclusão

### 📊 **Analytics & Dashboard**

Baseado nas interfaces enviadas no arquivo:

* `/analytics/overview` → visão geral da aplicação
* `/analytics/tasks` → estatísticas e tendências
* `/analytics/members` → análise de produtividade
* `/analytics/projects` → progresso dos projetos
* `/dashboard` → total de membros, tarefas ativas, conclusão, etc.
* `/dashboard/recent-activities` → últimas atividades

### 🔐 **Autenticação**

* Login com JWT
* Refresh Token
* Recuperação de senha
* Rotas públicas e privadas configuradas via Spring Security
* Filtro JWT personalizado

### 🛡 **Segurança**

* Spring Security
* JWT
* Rotas protegidas via Bearer Token
* GlobalExceptionHandler para padronizar erros

### 📦 **Infraestrutura**

* Docker + Docker Compose
* Flyway para versionamento automático das tabelas
* Banco PostgreSQL em produção
* H2 em ambiente de desenvolvimento
* Deploy completo no Render

---

<br>

# 🛠 **Tecnologias Utilizadas**

### 📚 **Backend**

| Tecnologia                      | Uso                        |
| ------------------------------- | -------------------------- |
| **Java 22 (OpenJDK)**           | Linguagem principal        |
| **Spring Boot**                 | Framework                  |
| **Spring Security**             | Autenticação e autorização |
| **JWT (auth0/java-jwt)**        | Tokens de acesso           |
| **Spring Data JPA**             | ORM                        |
| **Hibernate Validator**         | Validações                 |
| **SpringDoc OpenAPI (Swagger)** | Documentação da API        |
| **Flyway**                      | Migrações do banco         |
| **Slf4j**                       | Logging                    |

### 🧪 **Testes**

* **JUnit**
* **Mockito**
* **Spring Security Test**
* Testes unitários do service e da lógica de negócio

### 🐳 **Infra**

* Docker
* Docker Compose
* PostgreSQL
* Render (deploy)

---

<br>

# 📂 **Estrutura do Projeto**

```bash
src/
 ├── main/
 │    ├── java/com/.../teammanagement/
 │    │     ├── config/          # Configurações (security, swagger, CORS, etc.)
 │    │     ├── controller/      # Controllers REST
 │    │     ├── service/         # Regras de negócio
 │    │     ├── repository/      # Repositórios JPA
 │    │     ├── entity/          # Entidades do JPA
 │    │     ├── dto/             # DTOs de request/response
 │    │     ├── security/        # JWT + Filters + Authentication
 │    │     ├── exception/       # GlobalExceptionHandler + CustomExceptions
 │    │     ├── mapper/          # Conversões (MapStruct ou manuais)
 │    │     └── util/            # Utilitários
 │    └── resources/
 │          ├── application.properties
 │          ├── db/migration/     # Scripts Flyway
 │          └── static / templates
 └── test/
      └── ...                     # JUnit + Mockito tests
```

---

<br>

# ⚙️ **Configuração do Ambiente**

Antes de rodar o projeto, **é obrigatório criar um arquivo `.env`** na raiz do repositório:

> ⚠️ Sem esse arquivo, o Docker Compose e o Spring Boot não conseguirão subir.

---

## 📄 **.env necessário**

```env
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=
SPRING_PROFILES_ACTIVE=
JWT_SECRET=
APP_SHOW_SQL=
APP_FORMAT_SQL=
PGADMIN_DEFAULT_EMAIL=
PGADMIN_DEFAULT_PASSWORD=
```

### 🔑 O que cada variável representa:

| Variável                 | Descrição                       |
| ------------------------ | ------------------------------- |
| DB_HOST                  | Host do PostgreSQL              |
| DB_PORT                  | Porta do banco                  |
| DB_NAME                  | Nome do banco                   |
| DB_USER                  | Usuário                         |
| DB_PASSWORD              | Senha                           |
| SPRING_PROFILES_ACTIVE   | dev, prod, docker               |
| JWT_SECRET               | Chave secreta para gerar tokens |
| APP_SHOW_SQL             | Mostrar queries? true/false     |
| APP_FORMAT_SQL           | Identar queries? true/false     |
| PGADMIN_DEFAULT_EMAIL    | E-mail de login no PGAdmin      |
| PGADMIN_DEFAULT_PASSWORD | Senha do PGAdmin                |

---

<br>

# 🐳 **Como Rodar com Docker**

```bash
docker-compose up --build
```

Isso irá iniciar:

* API
* PostgreSQL
* PGAdmin
* Flyway automaticamente
* Perfis configurados no `.env`

Quando terminar:

* API → [http://localhost:8080](http://localhost:8080)
* PGAdmin → [http://localhost:5050](http://localhost:5050)

---

<br>

# ▶️ **Rodando Localmente sem Docker**

```bash
./gradlew bootRun
```

Ou no Windows:

```bash
gradlew.bat bootRun
```

Para rodar testes:

```bash
./gradlew test
```

---

<br>

# 🔐 **Autenticação & Segurança**

A aplicação usa:

* Spring Security
* JWT (com refresh token)
* Filtro **JwtSecurityFilter**
* **UserDetailsService** personalizado
* Rota `/auth/login` pública

### Fluxo:

1. Usuário envia email + senha
2. Recebe **access token** e **refresh token**
3. Rotas protegidas exigem:

   ```
   Authorization: Bearer <token>
   ```
4. Quando expira → usa refresh token

---

<br>

# 📚 **Documentação — Swagger**

Assim que a API estiver rodando:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Geração automática com SpringDoc:
`org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13`

Todos os endpoints estão descritos, incluindo:

* Auth
* Members
* Tasks
* Projects
* Analytics
* Dashboard

---

<br>

# 🧩 **Endpoints Principais**

### 🔐 **Autenticação**

| Método | Rota                  | Descrição            |
| ------ | --------------------- | -------------------- |
| POST   | `/auth/login`         | Login com JWT        |
| POST   | `/auth/refresh-token` | Renovar token        |
| POST   | `/auth/recover`       | Recuperação de senha |

### 👥 **Membros**

| GET | `/members` | Lista membros |
| POST | `/members` | Cria membro |
| PUT | `/members/{id}` | Atualiza |
| DELETE | `/members/{id}` | Remove |

### 📝 **Tarefas**

| GET | `/tasks` | Lista |
| POST | `/tasks` | Cria |
| PUT | `/tasks/{id}` | Atualiza |
| DELETE | `/tasks/{id}` | Remove |

### 📊 **Analytics**

Baseado no arquivo enviado:

* `/analytics/overview`
* `/analytics/tasks?days=30`
* `/analytics/members?page=0&size=10`
* `/analytics/projects`

### 📌 **Dashboard**

* `/api/dashboard`
* `/api/dashboard/recent-activities`

---

<br>

# 🧪 **Testes Automatizados**

O projeto utiliza:

* **JUnit 5**
* **Mockito** para mocks
* **Spring Boot Test**
* **Spring Security Test** para endpoints autenticados

Exemplos testados:

* Services
* Regras de negócio
* Validadores
* SecurityFilterJwt
* Controllers com mocks

Para rodar:

```bash
./gradlew test
```

---

<br>

# 🗃 **Versionamento de Banco — Flyway**

Scripts em:

```
src/main/resources/db/migration/
```

Sempre com padrão:

```
V1__create_tables.sql
V2__insert_initial_data.sql
```

Toda vez que a API roda, o Flyway:

* Valida
* Aplica novas migrations
* Garante consistência entre ambientes

---

<br>

# ☁️ **Deploy — Render**

O projeto foi configurado para:

* Build automatizado
* Execução do Dockerfile
* Variáveis de ambiente no dashboard
* Banco PostgreSQL gerenciado

### Fluxo:

1. Push no GitHub → Render identifica
2. Build via Dockerfile
3. Flyway sobe banco
4. API fica disponível via https

---

<br>

# 📈 **Futuras Melhorias**

* 🔔 Notificações (Email/SMS/Webhook)
* 🧠 Relatórios inteligentes com IA
* 🎨 Tema dark/light na documentação Swagger
* ⏱ Auditoria completa de logs (quem criou, quando mudou, etc.)
* 🧵 Suporte a WebSockets para atualização em tempo real
* 🧳 Multi-tenancy (várias empresas usando a mesma API)

---

## 📬 Contato

Dúvidas ou sugestões? Abra uma **issue** no repositório ou entre em contato! ✉️

[GitHub Repositorio](//https://github.com/Gusta-0/team-management)

---
