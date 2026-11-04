# Clínica Odontológica Xavier

Sistema em evolução para gestão de uma clínica odontológica. A API REST oferece cadastro de pacientes e endereços, profissionais, serviços odontológicos e agendamentos, com persistência PostgreSQL via SQL explícito e autenticação JWT. O site público apresenta a clínica, os serviços e a equipe e permite solicitar uma consulta; a equipe gerencia a agenda na área administrativa.

## Stack atual

- Java 21, Maven e Spring Boot 3.5.x.
- PostgreSQL, Spring JDBC/JdbcTemplate e SQL explícito; sem JPA/Hibernate.
- Flyway para migrações versionadas em `src/main/resources/db/migration`.
- Spring Security OAuth2 Resource Server para validar JWT HS256, com assinatura, expiração, emissor e audiência.
- BCrypt para armazenar hashes de senha.
- H2 em memória somente no perfil de testes.

## Executar localmente

Requisitos: Java 21, Maven 3.9+ e Docker Compose.

1. Copie `.env.example` para `.env`.
2. Troque **todos** os valores de exemplo: `POSTGRES_PASSWORD`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `ADMIN_EMAIL` e `ADMIN_PASSWORD`. A senha administrativa deve ter ao menos 12 caracteres; o segredo JWT deve ter pelo menos 32 bytes.
3. Suba o banco: `docker compose up -d postgres`.
4. Execute a API na raiz: `mvn spring-boot:run`.
5. Abra `http://localhost:8080/` e entre com `ADMIN_EMAIL` e `ADMIN_PASSWORD`.

O arquivo `.env` é carregado pela aplicação localmente e também fornece variáveis ao Docker Compose. Não versione esse arquivo nem reutilize as credenciais de exemplo. Se o banco já tiver uma conta com o e-mail configurado, o bootstrap não redefine sua senha. Sem `ADMIN_EMAIL` e `ADMIN_PASSWORD`, nenhum usuário inicial será criado automaticamente.

## Autenticação e segurança

- `POST /api/auth/login` é o endpoint público para obter um access token.
- Todos os endpoints administrativos `/api/**` exigem `Authorization: Bearer <token>`. O login e as rotas de catálogo e solicitação em `/api/publico/**` são públicas.
- Os tokens expiram após 30 minutos e validam assinatura HS256, emissor e audiência.
- Senhas são armazenadas com BCrypt; o login retorna uma mensagem genérica para credenciais inválidas.
- A sessão do frontend mantém o token em `sessionStorage`, que é limpo ao sair ou quando a API retorna 401. Isso é uma solução inicial; a revisão contra XSS e a decisão de armazenamento devem preceder uso em produção.
- Guarde `JWT_SECRET` em um gestor de segredos em ambientes hospedados.
- Todos os usuários atuais têm acesso igual aos endpoints protegidos; perfis e permissões granulares ainda não foram implementados.

### Endpoints principais

| Método | Rota | Ação |
| --- | --- | --- |
| POST | `/api/auth/login` | Valida e-mail/senha e retorna JWT |
| GET | `/api/clientes` | Lista pacientes (JWT obrigatório) |
| POST | `/api/clientes` | Cadastra paciente com endereço (JWT obrigatório) |
| GET | `/api/clientes/{id}` | Consulta paciente (JWT obrigatório) |
| PUT | `/api/clientes/{id}` | Atualiza paciente (JWT obrigatório) |
| DELETE | `/api/clientes/{id}` | Exclui paciente (JWT obrigatório) |

A API também mantém operações de consulta, atualização e exclusão de endereços.
| Método | Rota | Ação |
| --- | --- | --- |
| GET | `/api/publico/profissionais` | Lista profissionais ativos (público) |
| GET | `/api/publico/servicos` | Lista serviços ativos (público) |
| POST | `/api/publico/agendamentos` | Solicita horário sujeito à confirmação (público) |
| GET | `/api/agendamentos` | Consulta a agenda (JWT obrigatório) |
| POST | `/api/agendamentos` | Cria consulta confirmada (JWT obrigatório) |
| PATCH | `/api/agendamentos/{id}` | Atualiza status de consulta (JWT obrigatório) |
| POST | `/api/profissionais` | Cadastra profissional (JWT obrigatório) |
| POST | `/api/servicos` | Cadastra serviço (JWT obrigatório) |

## Testes

Execute `mvn verify`. Os testes de integração usam H2 em memória com perfil `test`; eles validam o fluxo de autenticação e regras básicas do cadastro, mas não substituem uma execução de validação contra PostgreSQL real.

## IA Factory

1. Leia `input/application-story.md`.
2. Consulte `docs/memory-bank/project-context.md`, `docs/specs/non-functional-requirements.md` e `docs/architecture/target-architecture.md`.
3. Siga os prompts em `.github/prompts/`: protótipo → especificação → backend → frontend → revisão.
4. Consulte os agentes em `.github/agents/` e instruções em `.github/instructions/` e `.github/skills/`.
5. Atualize memória, especificações, testes e documentação quando decisões mudarem.

## Interface

A interface ativa fica em `src/main/resources/static/` e é servida pela API em `http://localhost:8080/`. A página pública de solicitação de horários fica em `/agendar.html`; a equipe acessa agenda e cadastros em `/equipe.html`.

Os profissionais iniciais e as descrições recebidas são fictícios e foram incluídos pela migração `V3__agenda_profissionais_servicos.sql`. Solicitações públicas ficam com status `SOLICITADO`; a confirmação é feita manualmente pela equipe. Envio automático de e-mail/SMS ainda depende da configuração de um provedor de notificações.

## Pendências antes de produção

- Validar a aplicação e as migrações em PostgreSQL real e planejar importação de dados legados H2, se houver.
- Implementar perfis/permissões granulares e gestão administrativa de usuários.
- Rever o armazenamento do token e adicionar proteções contra XSS, rate limiting e controles operacionais para login.
- Revisar o tratamento de observações clínicas e separar/limitar o acesso a dados de saúde.
- Configurar TLS, logs sem dados pessoais, backups, monitoramento e gestão de segredos.
- Não utilizar dados reais de pacientes antes de concluir a revisão de segurança e privacidade.

Consulte `docs/architecture/roadmap.md` para o roadmap completo.
