#!/bin/bash
set -euo pipefail

echo "==========================================="
echo "  Minecraft Monitor — Setup para Deploy"
echo "==========================================="
echo ""

# ---------- 1. Verificar pré-requisitos ----------
echo "1. Verificando pré-requisitos..."

command -v docker >/dev/null 2>&1 || { echo "ERRO: Docker não encontrado. Instale: https://docs.docker.com/get-docker/"; exit 1; }
command -v git >/dev/null 2>&1 || { echo "ERRO: Git não encontrado."; exit 1; }

echo "   Docker: OK"
echo "   Git: OK"
echo ""

# ---------- 2. Verificar .env ----------
echo "2. Verificando arquivo .env..."

if [ ! -f .env ]; then
    echo "   Arquivo .env não encontrado."
    echo "   Copiando de .env.example..."
    cp .env.example .env
    echo "   AVISO: Edite o .env com suas credenciais antes de continuar."
    echo ""
    echo "   Variáveis obrigatórias no .env:"
    echo "   - JWT_SECRET: gere com: openssl rand -base64 32"
    echo "   - DB_URL: jdbc:postgresql://localhost:5432/minecraft_monitor"
    echo "   - DB_USERNAME: postgres"
    echo "   - DB_PASSWORD: (sua senha local)"
    echo "   - APP_BOOTSTRAP_USERNAME: admin"
    echo "   - APP_BOOTSTRAP_PASSWORD: (uma senha forte)"
    echo ""
    echo "   Depois execute este script novamente."
    exit 0
else
    echo "   .env encontrado."
fi
echo ""

# ---------- 3. Verificar se os valores foram preenchidos ----------
echo "3. Verificando variáveis do .env..."

source .env

check_var() {
    local var_name=$1
    local var_value=${!var_name:-}
    if [ -z "$var_value" ] || [ "$var_value" = "change-me-in-production" ] || [ "$var_value" = "change-me" ]; then
        echo "   AVISO: $var_name não está configurado ou usa valor padrão."
        return 1
    fi
    return 0
}

ERRORS=0
check_var "JWT_SECRET" || ERRORS=$((ERRORS + 1))
check_var "APP_BOOTSTRAP_PASSWORD" || ERRORS=$((ERRORS + 1))

if [ "$ERRORS" -gt 0 ]; then
    echo ""
    echo "   Edite o .env e configure as variáveis marcadas acima."
    exit 1
fi
echo "   Variáveis OK."
echo ""

# ---------- 4. Build local ----------
echo "4. Buildando backend..."
./mvnw clean package -DskipTests -q
echo "   Backend buildado: target/minecraft-monitor-0.0.1-SNAPSHOT.jar"
echo ""

echo "5. Buildando frontend..."
cd frontend
npm ci --silent
npm run build --silent
echo "   Frontend buildado: frontend/dist/"
cd ..
echo ""

# ---------- 6. Docker build ----------
echo "6. Buildando imagem Docker..."
docker build -t minecraft-monitor:latest .
echo "   Imagem Docker criada: minecraft-monitor:latest"
echo ""

# ---------- 7. Testar localmente ----------
echo "7. Testando localmente com docker-compose..."
docker compose down 2>/dev/null || true
docker compose up -d --build

echo "   Aguardando containers iniciarem..."
sleep 20

BACKEND_HEALTH=$(docker inspect --format='{{.State.Health.Status}}' minecraft-monitor-backend-1 2>/dev/null || echo "unknown")
echo "   Backend: $BACKEND_HEALTH"

if [ "$BACKEND_HEALTH" = "healthy" ]; then
    echo ""
    echo "   SUCESSO! Backend está saudável."
    echo "   Backend: http://localhost:8080"
    echo "   Frontend: http://localhost:3000"
    echo "   Health: http://localhost:8080/actuator/health"
else
    echo ""
    echo "   AVISO: Backend pode não estar pronto ainda."
    echo   "   Verifique com: docker compose logs backend"
fi

echo ""
echo "==========================================="
echo "  Setup local concluído!"
echo "==========================================="
echo ""
echo "Próximos passos para deploy em produção:"
echo ""
echo "1. Render (PostgreSQL + Backend):"
echo "   - Crie um PostgreSQL no Render"
echo "   - Crie um Web Service (Docker) no Render"
echo "   - Configure as variáveis de ambiente (veja DEPLOY.md)"
echo "   - OU use o render.yaml (Blueprint) para configuração automática"
echo ""
echo "2. Vercel (Frontend):"
echo "   - Crie um projeto no Vercel"
echo "   - Root directory: ./frontend"
echo "   - Edite frontend/vercel.json com a URL do backend Render"
echo "   - Faça deploy"
echo ""
echo "3. CORS:"
echo "   - Atualize CORS_ALLOWED_ORIGINS no Render com a URL do Vercel"
echo ""
echo "Guia completo: DEPLOY.md"
