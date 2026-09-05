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
=======
# Minecraft Monitor

API REST e dashboard web para monitoramento de servidores Minecraft.

O projeto permite cadastrar servidores Minecraft, verificar seu estado de funcionamento, acompanhar jogadores e latência, armazenar histórico das verificações, calcular estatísticas de disponibilidade e registrar eventos de mudança de estado.

## 🚧 Status

**Em desenvolvimento**

Atualmente o projeto já possui:

* Cadastro, consulta, atualização e remoção de servidores
* Monitoramento automático dos servidores
* Minecraft Server List Ping
* Histórico de status
* Estatísticas de disponibilidade
* Detecção de eventos `SERVER_UP` e `SERVER_DOWN`
* Paginação de histórico e eventos
* Filtros de eventos
* Tratamento de erros HTTP
* Validação dos dados de entrada
* Dashboard web em React
* Visualização de status, jogadores, latência, histórico, eventos e gráficos

## 🏗️ Arquitetura

```text
                    ┌─────────────────────┐
                    │     Dashboard       │
                    │   React + Vite      │
                    └──────────┬──────────┘
                               │
                               │ HTTP / REST
                               ▼
                    ┌─────────────────────┐
                    │     Spring Boot     │
                    │       REST API      │
                    └──────────┬──────────┘
                               │
                ┌──────────────┼──────────────┐
                │              │              │
                ▼              ▼              ▼
         ┌───────────┐  ┌─────────────┐  ┌───────────┐
         │ PostgreSQL│  │Minecraft Ping│  │ Scheduler │
         └───────────┘  └──────┬──────┘  └───────────┘
                               │
                               ▼
                       ┌─────────────┐
                       │   Minecraft │
                       │   Server    │
                       └─────────────┘
```

## 🛠️ Tecnologias

### Backend

* Java
* Spring Boot
* Spring Data JPA
* Spring Web
* Jakarta Validation
* Maven
* PostgreSQL

### Frontend

* React
* Vite
* JavaScript
* CSS
* Recharts

## 📁 Estrutura do projeto

```text
minecraft-monitor/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/lucas/minecraft_monitor/
│       │       ├── controller/
│       │       ├── service/
│       │       ├── repository/
│       │       ├── model/
│       │       ├── dto/
│       │       ├── scheduler/
│       │       ├── exception/
│       │       └── config/
│       │
│       └── resources/
│
├── frontend/
│   ├── src/
│   ├── package.json
│   └── ...
│
├── pom.xml
└── README.md
```

## 📡 Principais endpoints

### Servidores

```http
POST   /api/servers
GET    /api/servers
GET    /api/servers/{id}
PUT    /api/servers/{id}
DELETE /api/servers/{id}
```

### Status

```http
GET /api/servers/{id}/status
```

Exemplo:

```json
{
  "online": true,
  "host": "example.com",
  "port": 25565,
  "playersOnline": 2,
  "maxPlayers": 20,
  "version": "Paper 26.2",
  "latency": 242,
  "motd": "Meu servidor"
}
```

### Histórico

```http
GET /api/servers/{id}/history?page=0&size=20
```

### Estatísticas

```http
GET /api/servers/{id}/statistics?hours=24
```

Exemplo:

```json
{
  "periodHours": 24,
  "totalChecks": 100,
  "onlineChecks": 98,
  "offlineChecks": 2,
  "uptimePercentage": 98.0,
  "averagePlayers": 3.2,
  "peakPlayers": 8,
  "averageLatency": 245.4
}
```

### Eventos

```http
GET /api/servers/{id}/events?page=0&size=20
```

Também é possível filtrar por tipo:

```http
GET /api/servers/{id}/events?type=SERVER_DOWN&page=0&size=20
```

Tipos atualmente utilizados:

```text
SERVER_UP
SERVER_DOWN
```

## ⏱️ Monitoramento automático

O sistema realiza verificações periódicas dos servidores através de um scheduler do Spring.

Atualmente a verificação ocorre a cada:

```text
30 segundos
```

Cada verificação pode registrar:

* Estado do servidor
* Jogadores online
* Jogadores máximos
* Latência
* Versão
* Data e hora da verificação

Quando ocorre uma mudança de estado, um evento é registrado.

Exemplo:

```text
ONLINE
   ↓
ONLINE
   ↓
ONLINE
   ↓
OFFLINE
   ↓
SERVER_DOWN
   ↓
ONLINE
   ↓
SERVER_UP
```

## 🖥️ Dashboard

O frontend React fornece uma interface para visualizar os servidores monitorados.

Atualmente inclui:

* Lista de servidores
* Estado atual
* Número de jogadores
* Latência
* Versão do servidor
* Estatísticas
* Histórico de verificações
* Eventos
* Gráfico de latência
* Gráfico de jogadores
* Atualização automática

## 🚀 Como executar

### Backend

Na raiz do projeto:

```bash
./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

### Frontend

Entre na pasta:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Execute:

```bash
npm run dev
```

O dashboard ficará disponível em:

```text
http://localhost:5173
```

## 🗄️ Banco de dados

O projeto utiliza PostgreSQL.

Antes de executar a aplicação, configure as propriedades de conexão no:

```text
src/main/resources/application.properties
```

Exemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/minecraft_monitor
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

**Não versionar senhas ou outras credenciais no repositório.**

Utilize variáveis de ambiente para informações sensíveis em ambientes reais.

## 🔐 Tratamento de erros

A API possui tratamento centralizado de exceções.

Exemplo para um servidor inexistente:

```http
GET /api/servers/999
```

Resposta:

```json
{
  "status": 404,
  "message": "Servidor não encontrado",
  "timestamp": "2026-09-03T20:00:00"
}
```

Dados inválidos também são rejeitados pela API com `400 Bad Request`.

## 🧪 Testes

O projeto está sendo desenvolvido utilizando testes manuais dos endpoints através de ferramentas como:

* Postman
* cURL

Exemplo:

```bash
curl http://localhost:8080/api/servers
```

## 🔮 Próximos passos

O projeto continuará evoluindo com funcionalidades como:

* DTOs específicos para requests e responses
* API de métricas otimizada para gráficos
* RCON
* Execução de comandos no servidor
* Autenticação e autorização
* Alertas
* Integração com Discord/Webhooks
* Monitoramento avançado de latência
* Testes automatizados
* Docker
* Deploy
* Melhorias no dashboard

## 🎯 Objetivo

O objetivo do projeto é construir uma aplicação completa de monitoramento e gerenciamento de servidores Minecraft, servindo também como projeto prático para aprofundar conhecimentos em:

```text
Java
Spring Boot
REST APIs
JPA/Hibernate
PostgreSQL
React
Arquitetura de software
Monitoramento
Integração entre sistemas
```

## 👨‍💻 Autor

**Lucas Alexandre**

Projeto desenvolvido para estudo e evolução prática em desenvolvimento backend e frontend.
