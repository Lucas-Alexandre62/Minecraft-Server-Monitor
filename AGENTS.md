# Minecraft Server Monitor — Agent Instructions

## Objetivo

Este repositório contém uma aplicação de monitoramento de servidores Minecraft composta por:

* Backend Java/Spring Boot
* PostgreSQL
* Frontend React/Vite
* Dashboard de monitoramento

O objetivo do agente é evoluir o projeto continuamente, implementando as tarefas de `TASKS.md`, mantendo o código funcional, testando as alterações e evitando regressões.

---

## Regras gerais

1. Leia o código existente antes de modificar qualquer arquivo.
2. Nunca invente a estrutura atual do projeto.
3. Preserve funcionalidades existentes.
4. Faça alterações pequenas e relacionadas à tarefa atual.
5. Não reescreva arquivos inteiros sem necessidade.
6. Não remova métodos existentes sem verificar todos os usos.
7. Procure referências (`grep`, IDE ou equivalente) antes de alterar métodos públicos.
8. Nunca coloque senhas, tokens, chaves privadas, webhooks ou outras credenciais no código ou no Git.
9. Nunca use `git push --force` sem instrução explícita.
10. Não marque uma tarefa como concluída se os critérios de aceite não forem satisfeitos.
11. Ao encontrar um problema fora do escopo, registre-o em `TASKS.md` como tarefa futura em vez de alterar arbitrariamente o escopo.
12. Prefira soluções simples, legíveis e compatíveis com a arquitetura existente.

---

## Processo obrigatório por tarefa

Para cada tarefa:

1. Ler `TASKS.md`.
2. Identificar a primeira tarefa `TODO` cujas dependências estejam concluídas.
3. Inspecionar os arquivos relacionados antes de editar.
4. Planejar brevemente a implementação.
5. Implementar a menor alteração necessária.
6. Executar os testes e verificações relevantes.
7. Corrigir erros encontrados.
8. Reexecutar os testes.
9. Revisar o diff com `git diff`.
10. Atualizar a tarefa em `TASKS.md`.
11. Criar um commit pequeno e descritivo.
12. Passar para a próxima tarefa disponível.

---

## Backend

Tecnologias principais:

* Java
* Spring Boot
* Spring Data JPA
* Spring Security
* PostgreSQL
* Maven

### Após alterações no backend

Executar:

```bash
./mvnw test
```

Se não houver testes suficientes para a alteração, executar também:

```bash
./mvnw clean package
```

Verificar problemas de compilação, injeção de dependência, JPA e configuração.

Nunca alterar a configuração do banco sem verificar impacto sobre o restante da aplicação.

---

## Frontend

Tecnologias principais:

* React
* Vite
* JavaScript
* CSS
* React Router
* Recharts

### Após alterações no frontend

Executar:

```bash
npm install
npm run build
```

Na pasta:

```text
frontend/
```

Quando apropriado, também executar testes existentes.

---

## API

A API existente utiliza:

```text
/api/servers
```

Principais recursos:

```text
POST   /api/servers
GET    /api/servers
GET    /api/servers/{id}
PUT    /api/servers/{id}
DELETE /api/servers/{id}

GET /api/servers/{id}/status
GET /api/servers/{id}/history
GET /api/servers/{id}/statistics
GET /api/servers/{id}/events
GET /api/servers/{id}/metrics

POST /api/auth/login
```

Antes de alterar um endpoint, verificar:

* Controller
* Service
* Repository
* DTO
* validações
* tratamento de exceções
* consumidores no frontend

---

## Segurança

Nunca:

* commitar `JWT_SECRET`
* commitar senha do PostgreSQL
* commitar senha RCON
* commitar webhook do Discord
* colocar tokens diretamente no React
* colocar credenciais reais em exemplos versionados

Use variáveis de ambiente.

Arquivos locais contendo segredos devem estar no `.gitignore`.

---

## Banco de dados

Evitar mudanças destrutivas.

Nunca:

```text
DROP TABLE
DROP DATABASE
DELETE massivo
```

sem necessidade explícita da tarefa.

Antes de alterar entidades JPA, verificar relacionamentos e consultas existentes.

---

## Git

Use commits pequenos e semânticos.

Formato preferencial:

```text
feat: add metrics endpoint
feat: add server management form
fix: handle missing server
refactor: extract chart component
test: add server service tests
docs: update README
```

Antes do commit:

```bash
git status
git diff
```

Após o commit:

```bash
git status
```

Não incluir arquivos de build, IDE ou segredos.

---

## Qualidade

Prioridades:

1. Código funcionando
2. Segurança
3. Testes
4. Legibilidade
5. Manutenibilidade
6. Performance
7. Refinamentos visuais

Não introduzir abstrações complexas sem necessidade.

---

## Comportamento autônomo

O agente deve continuar trabalhando enquanto houver uma tarefa objetiva e verificável disponível.

Não parar apenas porque encontrou um erro de implementação. Investigar, corrigir e testar.

Não pedir confirmação para decisões técnicas de baixo risco. Escolher a solução mais simples e compatível com o projeto.

Quando uma decisão exigir informação externa que não existe no repositório, registrar a dependência e seguir para outra tarefa independente.

---

## Critério de conclusão

Uma tarefa só pode ser marcada como concluída quando:

* implementação feita;
* projeto compila;
* testes relevantes passam;
* nenhum segredo foi introduzido;
* diff foi revisado;
* documentação necessária foi atualizada;
* commit foi criado.

Após concluir, atualizar `TASKS.md`.

