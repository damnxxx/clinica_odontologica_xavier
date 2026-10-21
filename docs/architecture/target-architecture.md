# Arquitetura-alvo

## Componentes
- **Web:** HTML semântico, CSS e JavaScript modular.
- **API:** Spring Boot REST com controllers, serviços de aplicação, validação e tratamento de erros.
- **Persistência:** Spring JDBC/JdbcClient com SQL explícito e PostgreSQL; não usar JPA/Hibernate.
- **Identidade:** Spring Security, JWT e autorização no backend.
- **Banco:** migrações SQL versionadas, constraints, índices e transações.
- **Qualidade:** testes unitários e integração, CI, documentação OpenAPI a ser adicionada.

## Limites de domínio planejados
1. Cadastro de pacientes/clientes e endereços (já existe parcialmente).
2. Identidade, usuários e perfis.
3. Dentistas e equipe de atendimento.
4. Agenda e consultas.
5. Prontuário e eventos clínicos, com autorização e trilha de auditoria.
6. Faturamento e relatórios, após especificação.

## Estratégia de migração
1. Adicionar infraestrutura e testes PostgreSQL.
2. Definir esquema SQL e migração inicial compatível com os dados atuais.
3. Substituir repositórios JPA por JDBC sem remover contratos HTTP existentes.
4. Migrar configuração de H2 para PostgreSQL por ambiente.
5. Introduzir identidade/JWT com endpoints públicos explicitamente allowlisted.
6. Adicionar frontend consumindo os contratos publicados.
7. Executar testes e revisar segurança antes de integrar.

## Decisões pendentes
- Papéis finais e matriz de permissões.
- Estratégia de renovação/revogação JWT.
- Requisitos de implantação, backup, retenção e recuperação.
- Política de auditoria detalhada e perfis de acesso a prontuário.
