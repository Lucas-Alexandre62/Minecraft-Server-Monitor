#!/bin/bash
set -euo pipefail

ENV_FILE=".env"

if [ ! -f "$ENV_FILE" ]; then
    echo "Arquivo .env nao encontrado."
    echo "Copie .env.example para .env e configure as variaveis."
    exit 1
fi

echo "=== Minecraft Server Monitor - Deploy ==="

echo ""
echo "1. Parando containers anteriores..."
docker compose down

echo ""
echo "2. Buildando e iniciando containers..."
docker compose up --build -d

echo ""
echo "3. Verificando status dos containers..."
docker compose ps

echo ""
echo "4. Verificando saude dos containers..."
sleep 15

BACKEND_HEALTH=$(docker inspect --format='{{.State.Health.Status}}' minecraft-monitor-backend-1 2>/dev/null || echo "unknown")
POSTGRES_HEALTH=$(docker inspect --format='{{.State.Health.Status}}' minecraft-monitor-postgres-1 2>/dev/null || echo "unknown")

echo "   Backend: $BACKEND_HEALTH"
echo "   Postgres: $POSTGRES_HEALTH"

echo ""
echo "5. Testando conectividade..."

ERRORS=0

if [ "$BACKEND_HEALTH" = "healthy" ]; then
    echo "   Backend: OK (healthy)"
elif curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "   Backend: OK (actuator responding)"
else
    echo "   Backend: FALHOU"
    ERRORS=$((ERRORS + 1))
fi

if [ "$POSTGRES_HEALTH" = "healthy" ]; then
    echo "   Postgres: OK (healthy)"
else
    echo "   Postgres: FALHOU"
    ERRORS=$((ERRORS + 1))
fi

if curl -sf http://localhost:3000 > /dev/null 2>&1; then
    echo "   Frontend: OK"
else
    echo "   Frontend: FALHOU"
    ERRORS=$((ERRORS + 1))
fi

echo ""
if [ "$ERRORS" -gt 0 ]; then
    echo "=== Deploy concluido com erros ($ERRORS servicos falharam) ==="
    exit 1
fi

echo "=== Deploy concluido com sucesso ==="
