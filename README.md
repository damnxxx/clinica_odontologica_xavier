# Clínica Odontológica Xavier

Sistema em evolução para gestão de uma clínica odontológica. O código funcional atual é uma API REST Spring Boot para clientes e endereços; a interface web, autenticação JWT e migração para PostgreSQL estão em etapas planejadas e não devem ser consideradas concluídas.

## Estado atual e stack

- Java 21, Maven e Spring Boot 3.5.x.
- API REST de clientes e endereços em camadas.
- **Persistência atual:** Spring Data JPA/Hibernate e H2.
- **Stack-alvo aprovada:** PostgreSQL, SQL explícito com Spring JDBC/JdbcClient (sem JPA/Hibernate), Spring Security + JWT e frontend HTML/CSS/JavaScript.
- A migração é incremental para evitar deixar o projeto incompatível entre etapas.

## Executar a API atual

- Requisitos: Java 21 e Maven 3.9+.
- Execute na raiz: `mvn spring-boot:run`.
- API: `http://localhost:8080`.
- Banco atual: H2 em arquivo `./data/clinica_uuid_cliente`.
- Console H2 atual: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/clinica_uuid_cliente`, usuário `sa`, sem senha). Este console é apenas para desenvolvimento local; deve ser desabilitado ao preparar configuração de produção.

## Operações da API atual

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

CPF deve ser único. Erros de validação retornam HTTP 400, recursos ausentes 404 e conflitos de regra 409.

## IA Factory

Este repositório agora contém uma estrutura inicial inspirada no fluxo de trabalho do projeto Clinicahans:

1. Leia `input/application-story.md` para entender o objetivo e as restrições.
2. Consulte `docs/memory-bank/project-context.md`, `docs/specs/non-functional-requirements.md` e `docs/architecture/target-architecture.md`.
3. Para uma nova funcionalidade, siga os prompts numerados em `.github/prompts/`: protótipo → especificação → backend → frontend → revisão.
4. Consulte os agentes em `.github/agents/` e as instruções especializadas em `.github/instructions/` e `.github/skills/`.
5. Atualize a memória, especificações, testes e documentação quando decisões mudarem.

O fluxo está configurado como documentação e prompts para assistentes compatíveis com arquivos de instrução de repositório. A disponibilidade de cada agente/prompt depende do editor ou assistente usado.

## Frontend demonstrativo

Abra `frontend/index.html` no navegador para visualizar um protótipo estático. Ele não chama a API, não realiza login e não persiste dados. Não insira informações reais de pacientes.

## PostgreSQL local

1. Configure as variáveis de ambiente usando `.env.example` como referência; não versione seu `.env`.
2. Defina uma senha local para `POSTGRES_PASSWORD`.
3. Execute `docker compose up -d postgres`.

O Compose prepara apenas o serviço PostgreSQL. A aplicação ainda não está conectada a ele nesta etapa. O arquivo `docs/specs/database-schema-draft.sql` é um rascunho documental, não uma migração executável.

## Segurança e privacidade

- Não versione senhas, tokens, chaves JWT ou dados reais.
- Não registre CPF, credenciais, tokens nem conteúdo clínico em logs.
- A autorização precisa ser aplicada no backend; a interface não é uma fronteira de segurança.
- Antes de disponibilizar em produção, implementar autenticação/autorização, migrar o banco, revisar a exposição do console H2 e adicionar testes de segurança.

## Roadmap

Veja `docs/architecture/roadmap.md`. A próxima fase técnica é migrar a persistência para PostgreSQL com migrações versionadas e SQL explícito, mantendo os contratos HTTP atuais, seguida de autenticação JWT e integração do frontend.
