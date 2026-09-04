# Minecraft-Server-Monitor

## Configuração

Defina as variáveis de ambiente antes de iniciar o backend. Arquivos `.env`
locais são ignorados pelo Git e não devem ser versionados.

| Variável | Obrigatória | Descrição |
| --- | --- | --- |
| `DB_PASSWORD` | Sim | Senha do usuário do PostgreSQL. |
| `DB_URL` | Não | URL JDBC; o padrão aponta para o banco local. |
| `DB_USERNAME` | Não | Usuário do PostgreSQL; o padrão é `postgres`. |
| `APP_USERNAME` | Não | Usuário inicial da aplicação. |
| `APP_PASSWORD` | Não | Senha do usuário inicial da aplicação. |
| `JWT_SECRET` | Sim | Chave usada para assinar tokens JWT. |
