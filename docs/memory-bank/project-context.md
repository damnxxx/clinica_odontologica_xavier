# Memória do projeto

## Estado conhecido
- Projeto Java 21 / Spring Boot 3.5.x / Maven.
- API REST CRUD de clientes e endereços.
- Persistência via PostgreSQL/Spring JDBC com SQL explícito e migrações Flyway.
- H2 é usado apenas no perfil de testes.
- Login JWT implementado; endpoints `/api/**` exigem bearer token, exceto `/api/auth/login`.
- Frontend servido pelo Spring Boot em `src/main/resources/static/`; suporta login, listagem e cadastro básico.
- O bootstrap do primeiro usuário administrativo é opcional via `ADMIN_EMAIL` e `ADMIN_PASSWORD`; a senha é armazenada com BCrypt.
- Ainda não há permissões granulares, gestão de usuários, refresh token ou migração automática de dados legados H2.

## Direção aprovada
- Evoluir em branch separada e integrar por pull request.
- Alvo: PostgreSQL, SQL explícito com Spring JDBC, JWT/Spring Security e frontend HTML/CSS/JavaScript.
- Construir e validar a IA Factory no próprio repositório, inspirada no fluxo da Clinicahans.

## Regras de colaboração
- Ler história, requisitos não funcionais, arquitetura e ADRs antes de mudanças.
- Manter o sistema compilável por etapa.
- Documentar decisões e lacunas; atualizar esta memória quando a estrutura mudar.
- Nunca incluir segredos nem dados reais de pacientes.
- Não assumir que dados legados H2 foram migrados automaticamente; planejar e testar essa importação antes de produção.
