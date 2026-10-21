# Roadmap incremental

## Fase 1 — Fundação
- [x] Estruturar história do produto, memória, requisitos e arquitetura-alvo.
- [x] Criar agentes, instruções, prompts de trabalho e skill PostgreSQL.
- [x] Adicionar Compose para PostgreSQL e exemplo de variáveis de ambiente.
- [x] Criar protótipo estático do frontend.
- [x] Documentar o esquema relacional inicial.
- [x] Adicionar workflow Maven CI.

## Fase 2 — Migração de persistência
- [x] Adicionar driver PostgreSQL e Flyway.
- [x] Criar migração SQL versionada inicial.
- [x] Implementar persistência com SQL explícito via Spring JDBC.
- [x] Remover dependência de JPA/Hibernate e H2 do runtime.
- [x] Adicionar testes de integração da camada de serviço usando H2 somente no perfil de testes.
- [x] Documentar execução local e manter os contratos HTTP existentes.
- [ ] Confirmar execução verde do build/testes no GitHub Actions.
- [ ] Validar a aplicação contra um PostgreSQL real e planejar a importação de dados legados H2, se existirem.

## Fase 3 — Identidade e interface
- [ ] Definir matriz de permissões e fluxo de autenticação.
- [ ] Implementar Spring Security/JWT com testes de acesso permitido/negado.
- [ ] Criar contratos de API e integrar a interface com endpoints reais.
- [ ] Adicionar verificações de segurança ao CI.

## Fase 4 — Domínios de negócio
- [ ] Dentistas e equipe de atendimento.
- [ ] Agenda e consultas.
- [ ] Prontuário com segregação de acesso e trilha de auditoria.
- [ ] Faturamento e relatórios após especificação.

## Critério de conclusão por fase
A fase só deve ser marcada como concluída quando implementação, testes, documentação e revisão estiverem alinhados. Itens marcados como implementados descrevem o conteúdo do repositório; não significam que a pipeline ou um PostgreSQL real já tenham sido executados nesta sessão.
