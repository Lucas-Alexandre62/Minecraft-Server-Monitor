# Deploy — Minecraft Server Monitor

Guia passo a passo para deploy em produção usando **Render** (backend + PostgreSQL) e **Vercel** (frontend).

---

## Pré-requisitos

* Conta no [Render](https://render.com)
* Conta no [Vercel](https://vercel.com)
* Repositório no GitHub
* Git configurado localmente

---

## Opção A: Deploy automático com Render Blueprint (Recomendado)

O repositório já inclui um `render.yaml` que configura tudo automaticamente.

### Passo 1 — Push para o GitHub

```bash
git add .
git commit -m "feat: prepare for production deploy"
git push
```

### Passo 2 — Criar Blueprint no Render

1. Acesse o Dashboard do Render.
2. Clique em **New** > **Blueprint**.
3. Conecte seu repositório GitHub.
4. O Render vai detectar o `render.yaml` e criar automaticamente:
   * Um PostgreSQL (`minecraft-monitor-db`)
   * Um Web Service (`minecraft-monitor`)
   * Todas as variáveis de ambiente (incluindo `JWT_SECRET` e `APP_BOOTSTRAP_PASSWORD` gerados automaticamente)

### Passo 3 — Ajustar CORS

Após o deploy, edite a variável `CORS_ALLOWED_ORIGINS` no Web Service do Render:

```
CORS_ALLOWED_ORIGINS=https://<seu-app>.vercel.app
```

### Passo 4 — Anotar a URL do backend

O Render fornece uma URL como:
```
https://minecraft-monitor-xxxx.onrender.com
```

Anote essa URL para usar no Vercel.

---

## Opção B: Deploy manual passo a passo

### 1. PostgreSQL no Render

1. Acesse o Dashboard do Render.
2. Clique em **New** > **PostgreSQL**.
3. Configurações:
   * **Name:** `minecraft-monitor-db`
   * **Database:** `minecraft_monitor`
   * **User:** `minecraft` (ou deixe o padrão)
   * **Region:** escolha a mais próxima
   * **Plan:** Free (ou superior conforme necessidade)
4. Clique em **Create Database**.
5. Após criar, copie o **Internal Database URL** (formato: `postgres://user:password@host:5432/dbname`).
6. Anote também as credenciais individuais (hostname, user, password, dbname).

### 2. Backend no Render (Web Service)

1. No Dashboard do Render, clique em **New** > **Web Service**.
2. Conecte seu repositório GitHub.
3. Configurações:
   * **Name:** `minecraft-monitor`
   * **Region:** mesma do banco
   * **Runtime:** `Docker`
   * **Dockerfile Path:** `./Dockerfile`
   * **Build Command:** (deixe em branco — o Dockerfile cuida do build)
   * **Start Command:** (deixe em branco — o ENTRYPOINT cuida do start)
4. Na seção **Environment Variables**, adicione:

| Variável | Valor |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `PORT` | `8080` |
| `DB_URL` | `jdbc:postgresql://<host>:5432/<dbname>` |
| `DB_USERNAME` | `<user do Render>` |
| `DB_PASSWORD` | `<password do Render>` |
| `JWT_SECRET` | `<string aleatória forte, mínimo 32 caracteres>` |
| `APP_BOOTSTRAP_USERNAME` | `admin` |
| `APP_BOOTSTRAP_PASSWORD` | `<senha forte para o admin>` |
| `CORS_ALLOWED_ORIGINS` | `https://<seu-app>.vercel.app` |

5. Variáveis opcionais (email, webhook, RCON):

| Variável | Descrição |
| --- | --- |
| `MAIL_HOST` | Host SMTP (ex: `smtp.gmail.com`) |
| `MAIL_PORT` | Porta SMTP (ex: `587`) |
| `MAIL_USERNAME` | Usuário SMTP |
| `MAIL_PASSWORD` | Senha SMTP (app password) |
| `MAIL_SMTP_AUTH` | `true` |
| `MAIL_SMTP_STARTTLS` | `true` |
| `ALERT_EMAIL_FROM` | E-mail remetente |
| `ALERT_EMAIL_TO` | E-mail destinatário |
| `ALERT_WEBHOOK_URL` | URL do webhook |
| `ALERT_LATENCY_THRESHOLD` | Limite de latência em ms |
| `RCON_PASSWORD` | Senha RCON |

6. Clique em **Create Web Service**.
7. Aguarde o build e deploy. O Render vai:
   * Baixar o repositório
   * Buildar a imagem Docker (Java 25 + Maven)
   * Iniciar o container
   * Verificar o healthcheck em `/actuator/health`

8. Verifique o URL do serviço (formato: `https://<name>.onrender.com`).

---

## 3. Frontend no Vercel

1. No Dashboard do Vercel, clique on **Add New** > **Project**.
2. Importe o repositório GitHub.
3. Na tela de configuração:
   * **Framework Preset:** `Vite`
   * **Root Directory:** `./frontend` (importante: aponte para a pasta do frontend)
   * **Build Command:** `npm run build`
   * **Output Directory:** `dist`
4. Na seção **Environment Variables**, não é necessário adicionar variáveis secrets.
5. Clique em **Deploy**.

### Configurar rewrite da API

O arquivo `frontend/vercel.json` já está configurado com rewrites:

```json
{
  "rewrites": [
    {
      "source": "/api/:path*",
      "destination": "https://minecraft-monitor.onrender.com/api/:path*"
    }
  ]
}
```

**IMPORTANTE:** Atualize o destino do rewrite com a URL real do seu backend no Render.

Após o primeiro deploy, edite o `vercel.json` com a URL correta e faça push novamente.

---

## 4. Configurar CORS no Backend

Após deploy, atualize a variável `CORS_ALLOWED_ORIGINS` no Render com a URL exata do frontend Vercel:

```
CORS_ALLOWED_ORIGINS=https://<seu-app>.vercel.app
```

Se precisar de múltiplas origens, separe por vírgula:

```
CORS_ALLOWED_ORIGINS=https://<app>.vercel.app,http://localhost:5173
```

---

## 5. Verificações pós-deploy

### Backend

1. Acesse `https://<name>.onrender.com/actuator/health`
   * Deve retornar `{"status":"UP"}`
2. Teste o login:
   ```bash
   curl -X POST https://<name>.onrender.com/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"<senha-do-admin>"}'
   ```
   * Deve retornar um token JWT

### Frontend

1. Acesse `https://<app>.vercel.app/login`
2. Faça login com as credenciais do admin
3. Verifique se o dashboard carrega corretamente
4. Verifique se as requisições à API funcionam (Network do navegador)

---

## 6. Geração de senha segura para JWT_SECRET

Use um dos métodos abaixo para gerar um JWT_SECRET forte:

```bash
# Opção 1: openssl
openssl rand -base64 32

# Opção 2: python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"

# Opção 3: uuid
cat /proc/sys/kernel/random/uuid
```

NUNCA use o valor padrão `minha-chave-secreta-local` em produção.

---

## 7. Variáveis de ambiente — Resumo

### Backend (Render)

| Variável | Obrigatória | Descrição |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Sim | `prod` |
| `PORT` | Sim | `8080` |
| `DB_URL` | Sim | JDBC URL do Render Postgres |
| `DB_USERNAME` | Sim | Usuário do banco |
| `DB_PASSWORD` | Sim | Senha do banco |
| `JWT_SECRET` | Sim | Chave secreta JWT (mín. 32 chars) |
| `APP_BOOTSTRAP_USERNAME` | Sim | Usuário admin inicial |
| `APP_BOOTSTRAP_PASSWORD` | Sim | Senha admin inicial |
| `CORS_ALLOWED_ORIGINS` | Sim | URL do frontend Vercel |
| `MAIL_HOST` | Não | Host SMTP |
| `MAIL_PORT` | Não | Porta SMTP |
| `MAIL_USERNAME` | Não | Usuário SMTP |
| `MAIL_PASSWORD` | Não | Senha SMTP |
| `ALERT_EMAIL_FROM` | Não | E-mail remetente |
| `ALERT_EMAIL_TO` | Não | E-mail destinatário |
| `ALERT_WEBHOOK_URL` | Não | URL webhook |
| `ALERT_LATENCY_THRESHOLD` | Não | Limite latência (ms) |
| `RCON_PASSWORD` | Não | Senha RCON |

### Frontend (Vercel)

Nenhuma variável de ambiente com segredos necessária. O frontend usa caminhos relativos `/api` que são redirecionados pelo `vercel.json`.

---

## 8. Solução de problemas

### Backend não inicia

* Verifique os logs no Render Dashboard
* Confirme que todas as variáveis obrigatórias estão configuradas
* Verifique se o `DB_URL` está no formato correto: `jdbc:postgresql://<host>:5432/<dbname>`

### Health check falha

* Verifique se `PORT` está definida como `8080`
* Verifique os logs do container

### Frontend não conecta à API

* Verifique se o `vercel.json` aponta para a URL correta do backend
* Verifique se `CORS_ALLOWED_ORIGINS` inclui a URL do Vercel
* Verifique o Network no navegador (F12)

### Erro de CORS

* Confirme que a URL exata do Vercel está em `CORS_ALLOWED_ORIGINS`
* URL deve incluir `https://` e não ter barra no final
