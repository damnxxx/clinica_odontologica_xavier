# Arquitetura-alvo

## Componentes
- **Web:** HTML semântico, CSS e JavaScript servido pela aplicação Spring Boot.
- **API:** Spring Boot REST com controllers, serviços de aplicação, validação e tratamento de erros.
- **Persistência:** Spring JDBC/JdbcTemplate com SQL explícito e PostgreSQL; não usar JPA/Hibernate.
- **Identidade:** Spring Security e JWT HS256 com assinatura, expiração, emissor e audiência validados; autorização granular ainda pendente.
- **Banco:** migrações SQL versionadas pelo Flyway, constraints, índices e transações.
- **Qualidade:** testes de integração e CI; documentação OpenAPI a ser adicionada.

## Estado implementado
1. Cadastro de clientes e endereços.
2. Persistência PostgreSQL com SQL explícito e migrações Flyway.
3. Login por e-mail/senha, hash BCrypt e access token JWT.
4. Proteção de endpoints `/api/**` com bearer token, exceto o login.
5. Frontend básico com login, listagem e cadastro de pacientes.

## Limites de domínio planejados
1. Cadastro de pacientes/clientes e endereços (implementado).
2. Identidade, usuários e perfis (login implementado; gestão de usuários e perfis pendentes).
3. Dentistas e equipe de atendimento.
4. Agenda e consultas.
5. Prontuário e eventos clínicos, com autorização e trilha de auditoria.
6. Faturamento e relatórios, após especificação.

## Estratégia de migração e evolução
1. Validar o ambiente e migrações em PostgreSQL real.
2. Planejar importação de dados legados H2, se houver.
3. Definir matriz de permissões e autorização por recurso.
4. Rever armazenamento do token no navegador e reforçar proteções contra XSS.
5. Adicionar testes de segurança, documentação OpenAPI e revisão antes de produção.

## Decisões pendentes
- Papéis finais e matriz de permissões.
- Estratégia de renovação/revogação JWT.
- Requisitos de implantação, backup, retenção e recuperação.
- Política de auditoria detalhada e perfis de acesso a prontuário.
