# Instruções do repositório

Antes de qualquer mudança, leia nesta ordem:
1. `input/application-story.md`
2. `docs/memory-bank/project-context.md`
3. `docs/specs/non-functional-requirements.md`
4. `docs/architecture/target-architecture.md`
5. As instruções específicas em `.github/instructions/` e a skill relevante.

## Regras obrigatórias
- Preserve `main`; trabalhe em branch separada e proponha PR.
- Inspecione o código real antes de afirmar que uma funcionalidade existe.
- Siga Java 21, Spring Boot, PostgreSQL e SQL explícito com Spring JDBC. A arquitetura alvo não usa JPA/Hibernate.
- Faça mudanças pequenas e coerentes; não misture migração de banco, autenticação e regras clínicas sem plano e testes.
- Use parâmetros SQL vinculados; nunca concatene entrada do usuário em SQL.
- Valide entradas na borda HTTP e aplique autorização no servidor, não só no frontend.
- Não exponha segredos, tokens, dados pessoais ou dados clínicos em logs/exemplos.
- Toda alteração de esquema precisa de migração versionada e teste relevante.
- Atualize documentação e testes junto com o código.
- Execute testes disponíveis. Se não puder executá-los, diga isso claramente; não declare sucesso sem evidência.
- Não adicione módulos ou regras de negócio apenas por suposição; documente perguntas em aberto.
