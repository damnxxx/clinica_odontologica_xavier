# ADR 0001 — Stack-alvo

- **Status:** aprovado como direção; implementação incremental.
- **Contexto:** a versão inicial usa JPA/Hibernate e H2, enquanto o projeto de referência usa PostgreSQL e SQL explícito.
- **Decisão:** direcionar o projeto para PostgreSQL, Spring JDBC/JdbcClient, Spring Security com JWT e frontend HTML/CSS/JavaScript.
- **Consequências:** será necessária migração cuidadosa de persistência, configuração, testes e documentação. Não remover JPA nem H2 antes de migrar os repositórios e validar a aplicação.
- **Alternativas consideradas:** manter H2/JPA (mais simples no curto prazo, mas diverge da estratégia aprovada); trocar toda a arquitetura de uma vez (maior risco de regressão).
