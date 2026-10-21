# Roadmap incremental

## Fase 1 — Fundação (branch atual)
- [x] Estruturar história do produto, memória, requisitos e arquitetura-alvo.
- [x] Criar agentes, instruções, prompts de trabalho e skill PostgreSQL.
- [x] Adicionar Compose para PostgreSQL e exemplo de variáveis de ambiente.
- [x] Criar protótipo estático do frontend.
- [x] Documentar rascunho do esquema relacional atual.
- [ ] Validar build e criar CI para o build Maven.

## Fase 2 — Migração de persistência
- [ ] Definir migrações versionadas e estratégia de cópia dos dados existentes.
- [ ] Adicionar driver PostgreSQL e ferramenta de migração (Flyway ou equivalente).
- [ ] Implementar repositórios com Spring JDBC e testes de integração PostgreSQL.
- [ ] Remover JPA/Hibernate e H2 somente após todos os usos terem sido migrados.
- [ ] Documentar execução local e compatibilidade de endpoints.

## Fase 3 — Identidade e interface
- [ ] Definir matriz de permissões e fluxo de autenticação.
- [ ] Implementar Spring Security/JWT com testes de acesso permitido/negado.
- [ ] Criar contratos de API e integrar a interface com endpoints reais.
- [ ] Adicionar CI para build, testes e verificações de segurança.

## Fase 4 — Domínios de negócio
- [ ] Dentistas e equipe de atendimento.
- [ ] Agenda e consultas.
- [ ] Prontuário com segregação de acesso e trilha de auditoria.
- [ ] Faturamento e relatórios após especificação.

## Critério de conclusão por fase
A fase só deve ser marcada como concluída quando implementação, testes, documentação e revisão estiverem alinhados. Este roadmap registra intenção, não comprova que os itens futuros já existem.
