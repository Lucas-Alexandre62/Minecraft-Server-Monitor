# Minecraft Server Monitor

Aplicação de monitoramento de servidores Minecraft com dashboard em tempo real.

## Arquitetura

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Frontend  │────▶│   Backend   │────▶│  PostgreSQL  │
│  React/Vite │     │ Spring Boot │     │             │
│  :3000      │     │ :8080       │     │ :5432       │
└─────────────┘     └─────────────┘     └─────────────┘
```

## Funcionalidades

- Cadastro e gerenciamento de servidores Minecraft
- Verificação automática a cada 30 segundos (status, jogadores, latência)
- Dashboard com métricas agregadas
- Histórico paginado de status
- Eventos com filtros (online, offline, latência elevada, instabilidade)
- Gráficos por período (1h, 6h, 24h, 7d, 30d)
- Alertas via log, webhook e e-mail
- Autenticação JWT

## Setup

### Pré-requisitos

- Java 25+
- Node.js 24+
- PostgreSQL 17+
- Docker (opcional)

### Local

```bash
# 1. Criar arquivo .env
cp .env.example .env
# Editar .env com suas credenciais

# 2. Backend
./mvnw spring-boot:run

# 3. Frontend
cd frontend
npm install
npm run dev
```

### Docker

```bash
cp .env.example .env
./deploy.sh
```

## Variáveis de ambiente

| Variável | Obrigatória | Descrição |
| --- | --- | --- |
| `DB_URL` | Não | URL JDBC do PostgreSQL |
| `DB_USERNAME` | Sim | Usuário do PostgreSQL |
| `DB_PASSWORD` | Sim | Senha do PostgreSQL |
| `APP_USERNAME` | Sim | Usuário administrador inicial |
| `APP_PASSWORD` | Sim | Senha do administrador (armazenada como hash) |
| `JWT_SECRET` | Sim | Chave para assinar tokens JWT |
| `ALERT_LATENCY_THRESHOLD` | Não | Limite de latência em ms (padrão: 500) |
| `ALERT_WEBHOOK_URL` | Não | URL do webhook para alertas |
| `ALERT_EMAIL_HOST` | Não | Host do servidor SMTP |
| `ALERT_EMAIL_PORT` | Não | Porta SMTP (padrão: 587) |
| `ALERT_EMAIL_FROM` | Não | E-mail remetente |
| `ALERT_EMAIL_TO` | Não | E-mail destinatário |

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

## Testes

```bash
# Backend (49 testes)
./mvnw test

# Frontend (13 testes)
cd frontend
npm run test
```

## Segurança

- Todas as credenciais via variáveis de ambiente
- Senhas armazenadas como hash (BCrypt)
- Autenticação via JWT
- Tokens no sessionStorage (nunca em localStorage)
- Senhas e webhooks nunca versionados
