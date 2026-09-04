# Minecraft-Server-Monitor

## Configuração

Defina as variáveis de ambiente antes de iniciar o backend. Arquivos `.env`
locais são ignorados pelo Git e não devem ser versionados.

| Variável | Obrigatória | Descrição |
| --- |  | --- |
| `DB_PASSWORD` | Sim | Senha do usuário do PostgreSQL. |
| `DB_URL` | Não | URL JDBC; o padrão aponta para o banco local. |
| `DB_USERNAME` | sIM | Usuário do PostgreSQL; o padrão é `postgres`. |
| `APP_USERNAME` | Sim | Usuário administrador inicial, criado no primeiro início. |
| `APP_PASSWORD` | Sim | Senha do administrador inicial; é armazenada somente como hash. |
| `JWT_SECRET` | Sim | Chave usada para assinar tokens JWT. |

O usuário inicial recebe o papel `ADMIN`. Usuários são persistidos na tabela
`application_users`; senhas nunca são armazenadas em texto puro. A autenticação
consulta essa tabela, enquanto a autorização usa o papel presente no JWT.
