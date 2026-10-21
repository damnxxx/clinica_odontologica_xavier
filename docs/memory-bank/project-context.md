# Memória do projeto

## Estado conhecido
- Projeto Java 21 / Spring Boot 3.5.x / Maven.
- API REST CRUD de clientes e endereços.
- Persistência atual do código: PostgreSQL via Spring JDBC/JdbcTemplate com SQL explícito; migrações via Flyway.
- H2 existe somente no escopo de testes, sob o perfil `test`.
- A interface `frontend/` ainda é um protótipo estático e não está integrada à API.
- Autenticação JWT/Spring Security ainda não foi implementada.

## Direção aprovada
- Evoluir em branch separada e integrar por pull request.
- Alvo: PostgreSQL, SQL explícito com Spring JDBC, JWT/Spring Security e frontend HTML/CSS/JavaScript.
- Construir e validar a IA Factory no próprio repositório, inspirada no fluxo da Clinicahans.

## Regras de colaboração
- Ler história, requisitos não funcionais, arquitetura e ADRs antes de mudanças.
- Manter o sistema compilável por etapa.
- Documentar decisões e lacunas; atualizar esta memória quando a estrutura mudar.
- Nunca incluir segredos nem dados reais de pacientes.
- Não assumir que dados legados do H2 foram migrados automaticamente; planejar e testar essa importação antes de produção.
