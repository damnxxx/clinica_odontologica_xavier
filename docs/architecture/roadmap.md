# Roadmap incremental

## Fase 1 — Fundação
- [x] Estruturar história do produto, memória, requisitos e arquitetura-alvo.
- [x] Criar agentes, instruções, prompts de trabalho e skill PostgreSQL.
- [x] Adicionar Compose para PostgreSQL e exemplo de variáveis de ambiente.
- [x] Criar protótipo estático do frontend.
- [x] Documentar o esquema relacional inicial.
- [x] Adicionar workflow Maven CI.

## Fase 2 — Persistência PostgreSQL
- [x] Adicionar driver PostgreSQL, Flyway e migração inicial versionada.
- [x] Implementar SQL explícito via Spring JDBC e remover JPA/Hibernate do runtime.
- [x] Adicionar testes de integração usando H2 somente no perfil de testes.
- [x] Confirmar build e testes no GitHub Actions.
- [ ] Validar em PostgreSQL real e planejar importação de dados legados H2, se existirem.

## Fase 3 — Identidade e interface
- [x] Implementar login com hash BCrypt e JWT HS256 com expiração.
- [x] Proteger endpoints `/api/**` com Spring Security e exigir bearer token, exceto login.
- [x] Adicionar bootstrap administrativo opcional por variáveis de ambiente.
- [x] Servir frontend pelo Spring Boot e conectar login/listagem/cadastro à API.
- [x] Adicionar testes de autenticação e autorização básica.
- [ ] Definir perfis e permissões granulares; todos os usuários atuais têm o mesmo nível de acesso.
- [ ] Revisar armazenamento do token no navegador e adicionar proteções adicionais contra XSS.
- [ ] Fazer revisão de segurança e validar fluxo completo em ambiente PostgreSQL real.

## Fase 4 — Domínios de negócio
- [ ] Dentistas e equipe de atendimento.
- [ ] Agenda e consultas.
- [ ] Prontuário com segregação de acesso e trilha de auditoria.
- [ ] Faturamento e relatórios após especificação.

## Critério de conclusão por fase
Itens marcados como implementados descrevem o conteúdo do repositório; não significam que o ambiente de produção foi validado. A aplicação não deve ser usada com dados reais até concluir a revisão de segurança, segregação de acesso a dados clínicos e validação operacional.
