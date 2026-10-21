# Clínica Odontológica Xavier

Sistema em evolução para gestão de uma clínica odontológica. A API REST oferece cadastro de clientes e endereços e agora usa PostgreSQL com SQL explícito via Spring JDBC. A autenticação JWT e a integração completa do frontend continuam em etapas futuras.

## Stack atual

- Java 21, Maven e Spring Boot 3.5.x.
- API REST em camadas, com validação de entrada.
- **Persistência:** PostgreSQL e Spring JDBC/JdbcTemplate; sem JPA/Hibernate.
- **Migrações:** Flyway, com SQL versionado em `src/main/resources/db/migration`.
- **Testes:** H2 em memória somente no perfil de testes; não é banco de produção.
- **Próximas etapas:** Spring Security + JWT, integração frontend/API e testes adicionais de segurança.

## Executar localmente

Requisitos: Java 21, Maven 3.9+ e Docker Compose.

1. Copie `.env.example` para `.env` e defina uma senha local para `POSTGRES_PASSWORD`.
2. Suba o banco: `docker compose up -d postgres`.
3. Confirme que o PostgreSQL está saudável: `docker compose ps`.
4. Execute a API: `mvn spring-boot:run`.

Configurações da API podem ser sobrescritas por variáveis de ambiente:

| Variável | Padrão local |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/clinica_xavier` |
| `DB_USERNAME` | `clinica_app` |
| `DB_PASSWORD` | vazio (defina no ambiente/.env antes de iniciar a API) |
| `DB_POOL_SIZE` | `10` |

A aplicação executa as migrações Flyway automaticamente ao iniciar. Não use credenciais reais em arquivos versionados. O arquivo `.env` deve permanecer local.

## Operações da API

| Método | Rota | Ação |
| --- | --- | --- |
| POST | `/api/clientes` | Cadastra cliente com pelo menos um endereço |
| GET | `/api/clientes` | Lista clientes com seus endereços |
| GET | `/api/clientes/{id}` | Consulta um cliente |
| PUT | `/api/clientes/{id}` | Atualiza dados e a lista de endereços do cliente |
| DELETE | `/api/clientes/{id}` | Exclui cliente e seus endereços |
| POST | `/api/clientes/{id}/enderecos` | Adiciona outro endereço |
| PUT | `/api/clientes/{id}/enderecos/{enderecoId}` | Altera um endereço |
| DELETE | `/api/clientes/{id}/enderecos/{enderecoId}` | Exclui endereço, desde que reste ao menos um |

CPF é único. Erros de validação retornam HTTP 400, recursos ausentes 404 e conflitos de regra 409.

## Testes

Execute `mvn test`. Os testes de integração usam H2 em memória no perfil `test`, sem substituir o PostgreSQL usado em execução normal.

## IA Factory

1. Leia `input/application-story.md` para entender objetivo e restrições.
2. Consulte `docs/memory-bank/project-context.md`, `docs/specs/non-functional-requirements.md` e `docs/architecture/target-architecture.md`.
3. Para uma nova funcionalidade, siga os prompts em `.github/prompts/`: protótipo → especificação → backend → frontend → revisão.
4. Consulte os agentes em `.github/agents/` e instruções em `.github/instructions/` e `.github/skills/`.
5. Atualize memória, especificações, testes e documentação quando decisões mudarem.

A disponibilidade de agentes/prompts depende do editor ou assistente usado.

## Frontend demonstrativo

Abra `frontend/index.html` no navegador para visualizar um protótipo estático. Ele ainda não chama a API, não realiza login e não persiste dados. Não insira informações reais de pacientes.

## Segurança e privacidade

- Não versione senhas, tokens, chaves JWT ou dados reais.
- Não registre CPF, credenciais, tokens nem conteúdo clínico em logs.
- A autorização precisa ser aplicada no backend; a interface não é uma fronteira de segurança.
- Antes de produção, implemente autenticação/autorização, revise o tratamento de observações clínicas e integre o frontend com a API.

Consulte `docs/architecture/roadmap.md` para o roadmap completo.
