# Minecraft Server Monitor — Task Board

Legenda:

* `TODO` — ainda não implementado
* `IN PROGRESS` — em desenvolvimento
* `DONE` — concluído e verificado
* `BLOCKED` — depende de algo externo

---

# Fase 1 — Backend

## T001 — Refatorar DTOs de Request/Response

Status: DONE
Dependências: nenhuma

Objetivo:
Separar entidades JPA dos objetos recebidos e retornados pela API.

Critérios de aceite:

* Criar DTO de criação
* Criar DTO de atualização
* Criar DTO de resposta
* POST não recebe entidade JPA diretamente
* PUT não recebe entidade JPA diretamente
* Testes existentes continuam passando

---

## T002 — Melhorar validação da API

Status: DONE
Dependências: T001

Critérios de aceite:

* Nome obrigatório
* Host obrigatório
* Porta entre 1 e 65535
* `hours` positivo
* `minutes` positivo
* `page` válido
* `size` válido
* Erros retornam HTTP 400 padronizado

---

## T003 — Padronizar tratamento de exceções

Status: DONE
Dependências: T002

Critérios de aceite:

* 400 para entrada inválida
* 404 para recurso inexistente
* 401 para não autenticado
* 403 para não autorizado
* 500 para erro inesperado
* Resposta JSON padronizada

---

## T004 — Melhorar endpoint de métricas

Status: DONE
Dependências: T003

Critérios de aceite:

* `/metrics` suporta períodos adequados para gráficos
* Não carregar dados desnecessários
* Ordenação temporal correta
* Endpoint eficiente para dashboard

---

# Fase 2 — Segurança

## T036 — Remover credencial de banco versionada

Status: DONE
Dependências: nenhuma

Critérios de aceite:

* Credencial migrada para variável de ambiente
* Arquivos locais de ambiente ignorados pelo Git
* Variáveis documentadas
* Histórico Git auditado

## T005 — Finalizar JWT

Status: DONE
Dependências: T003

Critérios de aceite:

* Login gera JWT
* Endpoints protegidos exigem Bearer token
* Token possui expiração
* Segredo vem de variável de ambiente
* Frontend não usa Basic Auth

---

## T006 — Melhorar autenticação

Status: DONE
Dependências: T005

Critérios de aceite:

* Estrutura preparada para múltiplos usuários
* Separação de autenticação e autorização
* Senhas nunca armazenadas em texto puro
* Documentar configuração

---

## T007 — Revisar segurança do frontend

Status: DONE
Dependências: T006

Critérios de aceite:

* Logout limpa sessão
* Rotas protegidas
* API centralizada
* Tratamento de token expirado
* Nenhum segredo no código-fonte

---

# Fase 3 — Monitoramento

## T008 — Melhorar eventos

Status: DONE
Dependências: T004

Critérios de aceite:

* SERVER_UP
* SERVER_DOWN
* Sem eventos duplicados
* Consulta paginada
* Filtros funcionando

---

## T009 — Detectar latência elevada

Status: TODO
Dependências: T008

Critérios de aceite:

* Limite configurável
* Evento de alta latência
* Não gerar spam de eventos
* Evento aparecer na API

---

## T010 — Detectar instabilidade

Status: TODO
Dependências: T009

Critérios de aceite:

* Detectar múltiplas mudanças de estado em janela de tempo
* Registrar evento
* Exibir no dashboard

---

# Fase 4 — Dashboard

## T011 — Refatorar componentes

Status: DONE
Dependências: nenhuma

---

## T012 — Dashboard principal

Status: DONE
Dependências: nenhuma

---

## T013 — Página de detalhes

Status: DONE
Dependências: T012

---

## T014 — CRUD pelo dashboard

Status: DONE
Dependências: T012

---

## T015 — Gráficos

Status: DONE
Dependências: T013

---

## T016 — Histórico paginado no dashboard

Status: DONE
Dependências: T004

Critérios de aceite:

* Paginação funcional
* Controle de página
* Controle de quantidade
* Navegação anterior/próxima

---

## T017 — Gráficos por período

Status: DONE
Dependências: T004

Critérios de aceite:

* 10 minutos
* 1 hora
* 6 horas
* 24 horas
* Atualização automática
* Sem múltiplas requisições desnecessárias

---

## T018 — Overview avançado

Status: DONE
Dependências: T017

Critérios de aceite:

* Servidores online
* Servidores offline
* Jogadores totais
* Latência média
* Disponibilidade

---

## T019 — Melhorar UX

Status: TODO
Dependências: T018

Critérios de aceite:

* Toasts
* Loading states
* Empty states
* Error states
* Responsividade
* Navegação consistente

---

# Fase 5 — RCON

## T020 — Preparar integração RCON

Status: BLOCKED
Dependências: T006

Motivo:
Precisa de um servidor Minecraft administrável para teste.

---

## T021 — Consultar informações via RCON

Status: BLOCKED
Dependências: T020

---

## T022 — Executar comandos RCON

Status: BLOCKED
Dependências: T021

---

## T023 — Proteger comandos RCON

Status: BLOCKED
Dependências: T022

---

# Fase 6 — Alertas

## T024 — Interface de alertas

Status: DONE
Dependências: T008

---

## T025 — Webhook genérico

Status: DONE
Dependências: T024

---

## T026 — Discord

Status: BLOCKED
Dependências: T025

Motivo:
É necessário um servidor/canal com permissão de webhook para testes.

---

## T027 — E-mail

Status: TODO
Dependências: T025

---

# Fase 7 — Testes

## T028 — Testes unitários dos Services

Status: TODO
Dependências: T003

---

## T029 — Testes dos Controllers

Status: TODO
Dependências: T028

---

## T030 — Testes do monitoramento

Status: TODO
Dependências: T029

Critérios de aceite:

* ONLINE → ONLINE não gera evento
* ONLINE → OFFLINE gera SERVER_DOWN
* OFFLINE → ONLINE gera SERVER_UP
* Primeiro estado não gera alerta

---

## T031 — Testes do frontend

Status: TODO
Dependências: T029

---

# Fase 8 — Produção

## T032 — Docker

Status: TODO
Dependências: T030

Critérios de aceite:

* Backend em container
* PostgreSQL em container
* Frontend preparado para produção

---

## T033 — Configuração por ambiente

Status: TODO
Dependências: T032

Critérios de aceite:

* Development
* Production
* Segredos por ambiente

---

## T034 — Deploy

Status: TODO
Dependências: T033

---

## T035 — Documentação final

Status: TODO
Dependências: T034

Critérios de aceite:

* README atualizado
* Arquitetura documentada
* Como executar
* Variáveis de ambiente
* API documentada
* Screenshots do dashboard

---

# Regra do backlog

Ao iniciar uma sessão:

1. Escolher a primeira tarefa `TODO` cujas dependências estejam `DONE`.
2. Implementar.
3. Testar.
4. Atualizar o status.
5. Criar commit.
6. Repetir.

Não trabalhar em tarefas `BLOCKED` sem que a dependência externa esteja disponível.
