# História da aplicação — Clínica Odontológica Xavier

## Objetivo
Construir um sistema web para apoiar a operação de uma clínica odontológica, com cadastro de pacientes, profissionais, equipe, agenda e, em etapas futuras, prontuário, faturamento e auditoria.

## Estado atual confirmado
O projeto existente é uma API Spring Boot com operações CRUD de clientes e endereços. A base usa Java 21, Maven, Spring Data JPA e H2. Não presumir que módulos futuros já existem.

## Direção técnica desejada
- Java 21 e Spring Boot.
- PostgreSQL como banco principal.
- SQL explícito via Spring JDBC/JdbcClient; não usar JPA/Hibernate na arquitetura alvo.
- Spring Security e autenticação JWT.
- Frontend servido como HTML, CSS e JavaScript sem framework obrigatório.
- Migrações de banco versionadas, testes automatizados e configuração por variáveis de ambiente.
- Segredos nunca devem ser commitados.

## Princípios do domínio
- Separar dados cadastrais de dados clínicos sensíveis.
- Aplicar menor privilégio e autorização no servidor.
- Não registrar CPF, tokens, credenciais nem conteúdo de prontuário em logs.
- Mudanças em registros clínicos finalizados devem preservar histórico; preferir retificação/adendo auditável.
- Não inventar regras clínicas ou regulatórias. Registrar dúvidas em decisões pendentes.

## Ordem de evolução
1. Estabelecer IA Factory, arquitetura-alvo e documentação.
2. Migrar persistência de clientes/endereços para PostgreSQL e SQL explícito.
3. Implementar identidade, autenticação JWT e autorização por perfil.
4. Construir frontend e integrar com API.
5. Adicionar dentistas, atendentes e consultas; depois prontuário e módulos operacionais.
6. Validar testes, segurança, migrações e documentação antes de cada entrega.
