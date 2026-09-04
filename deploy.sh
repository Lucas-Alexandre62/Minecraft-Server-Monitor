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
sleep 10

BACKEND_HEALTH=$(docker inspect --format='{{.State.Health.Status}}' minecraft-monitor-backend-1 2>/dev/null || echo "unknown")
POSTGRES_HEALTH=$(docker inspect --format='{{.State.Health.Status}}' minecraft-monitor-postgres-1 2>/dev/null || echo "unknown")

echo "   Backend: $BACKEND_HEALTH"
echo "   Postgres: $POSTGRES_HEALTH"

echo ""
echo "5. Testando conectividade..."
if curl -sf http://localhost:8080/api/servers > /dev/null 2>&1; then
    echo "   Backend: OK"
else
    echo "   Backend: FALHOU"
fi

if curl -sf http://localhost:3000 > /dev/null 2>&1; then
    echo "   Frontend: OK"
else
    echo "   Frontend: FALHOU"
fi

echo ""
echo "=== Deploy concluido ==="
