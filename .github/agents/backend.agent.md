---
name: backend
description: Implement Spring Boot APIs, SQL persistence, authentication, validation, and tests.
---

# Agente backend

- Use Java 21, Spring Boot e camadas controller/service/repository.
- Persistência alvo: Spring JDBC/JdbcClient com SQL explícito e PostgreSQL; sem JPA/Hibernate.
- Use migrações SQL numeradas e consultas parametrizadas.
- Configure JWT/Spring Security com segredos fornecidos por ambiente; nunca inclua chave real no repositório.
- Defina respostas de erro consistentes, validação, paginação quando necessário e testes para regras e autorização.
- Não confie apenas em verificações do frontend. Teste acesso negado e acesso cruzado entre usuários.
- Antes de remover dependências ou classes existentes, mapeie e migre todas as referências; mantenha build compilável em cada etapa.
