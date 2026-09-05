# Minecraft Server Monitor

API REST e dashboard web para monitoramento de servidores Minecraft.

O projeto permite cadastrar servidores Minecraft, verificar seu estado de funcionamento, acompanhar jogadores e latência, armazenar histórico das verificações, calcular estatísticas de disponibilidade e registrar eventos de mudança de estado.

## Arquitetura

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Frontend  │────▶│   Backend   │────▶│  PostgreSQL  │
│  React/Vite │     │ Spring Boot │     │             │
│  :3000      │     │ :8080       │     │ :5432       │
└─────────────┘     └─────────────┘     └─────────────┘
```

## Tecnologias

### Backend

* Java 25
* Spring Boot
* Spring Data JPA
* Spring Security (JWT)
* Spring Boot Actuator
* PostgreSQL 17
* Maven

### Frontend

* React
* Vite
* JavaScript
* CSS
* Recharts

### Infraestrutura

* Docker / Docker Compose
* Nginx (servindo frontend)

## Estrutura do projeto

```
minecraft-monitor/
├── src/
│   └── main/
│       ├── java/com/lucas/minecraft_monitor/
│       │   ├── config/          # Security, CORS
│       │   ├── controller/      # REST controllers
│       │   ├── dto/             # Request/Response DTOs
│       │   ├── exception/       # Exceções e handlers
│       │   ├── model/           # Entidades JPA
│       │   ├── repository/      # Spring Data repos
│       │   ├── scheduler/       # Monitoramento automático
│       │   └── service/         # Lógica de negócio
│       └── resources/
│           ├── application.properties
│           ├── application-dev.yml
│           └── application-prod.yml
├── frontend/
│   ├── src/
│   ├── Dockerfile
│   └── nginx.conf
├── Dockerfile
├── docker-compose.yml
├── deploy.sh
├── .env.example
└── pom.xml
```

## Como executar

### Local (desenvolvimento)

```bash
# 1. Configurar variáveis de ambiente
cp .env.example .env
# Editar .env com suas credenciais

# 2. Backend
./mvnw spring-boot:run

# 3. Frontend
cd frontend
npm install
npm run dev
```

Backend: `http://localhost:8080`
Frontend: `http://localhost:5173`

### Docker

```bash
cp .env.example .env
# Editar .env com suas credenciais
./deploy.sh
```

Ou manualmente:

```bash
cp .env.example .env
docker compose up --build -d
```

Frontend: `http://localhost:3000`
Backend: `http://localhost:8080`

## Variáveis de ambiente

| Variável | Obrigatória | Descrição |
| --- | --- | --- |
| `POSTGRES_USER` | Não | Usuário do PostgreSQL (padrão: minecraft) |
| `POSTGRES_PASSWORD` | Não | Senha do PostgreSQL (padrão: minecraft) |
| `POSTGRES_DB` | Não | Nome do banco (padrão: minecraft_monitor) |
| `JWT_SECRET` | Sim | Chave para assinar tokens JWT |
| `RCON_PASSWORD` | Não | Senha RCON do servidor Minecraft |
| `ALERT_WEBHOOK_URL` | Não | URL do webhook para alertas |
| `ALERT_EMAIL_HOST` | Não | Host do servidor SMTP |
| `ALERT_EMAIL_PORT` | Não | Porta SMTP (padrão: 587) |
| `ALERT_EMAIL_FROM` | Não | E-mail remetente |
| `ALERT_EMAIL_TO` | Não | E-mail destinatário |
| `ALERT_LATENCY_THRESHOLD` | Não | Limite de latência em ms (padrão: 500) |

## Autenticação JWT

A API utiliza autenticação via JWT (JSON Web Token).

* Endpoint de login: `POST /api/auth/login`
* Todos os demais endpoints exigem header `Authorization: Bearer <token>`
* Tokens possuem expiração configurável
* Senhas armazenadas com hash BCrypt

## Endpoints da API

### Autenticação

| Método | Endpoint | Descrição |
| --- | --- | --- |
| POST | `/api/auth/login` | Login (retorna JWT) |

### Servidores

| Método | Endpoint | Descrição |
| --- | --- | --- |
| POST | `/api/servers` | Criar servidor |
| GET | `/api/servers` | Listar servidores |
| GET | `/api/servers/{id}` | Detalhes do servidor |
| PUT | `/api/servers/{id}` | Atualizar servidor |
| DELETE | `/api/servers/{id}` | Remover servidor |

### Monitoramento

| Método | Endpoint | Descrição |
| --- | --- | --- |
| GET | `/api/servers/{id}/status` | Status atual |
| GET | `/api/servers/{id}/history` | Histórico paginado |
| GET | `/api/servers/{id}/statistics` | Estatísticas |
| GET | `/api/servers/{id}/events` | Eventos paginados |
| GET | `/api/servers/{id}/metrics` | Métricas por período |

### Alertas

| Método | Endpoint | Descrição |
| --- | --- | --- |
| GET | `/api/alerts` | Listar configurações |
| POST | `/api/alerts` | Criar configuração |
| PUT | `/api/alerts/{id}` | Atualizar configuração |
| DELETE | `/api/alerts/{id}` | Remover configuração |

### Health

| Método | Endpoint | Descrição |
| --- | --- | --- |
| GET | `/actuator/health` | Status de saúde (público) |

## Testes

```bash
# Backend
./mvnw test

# Frontend
cd frontend
npm run test
```

## Deploy

```bash
./deploy.sh
```

O script executa:

1. `docker compose down` — para containers anteriores
2. `docker compose up --build -d` — builda e inicia containers
3. Verifica healthchecks de backend, postgres e frontend
4. Retorna código de erro se algum serviço falhar

## Monitoramento automático

O scheduler do Spring realiza verificações periódicas dos servidores a cada 30 segundos, registrando:

* Estado (online/offline)
* Jogadores online e máximo
* Latência
* Versão do servidor

Mudanças de estado geram eventos `SERVER_UP` e `SERVER_DOWN`.
