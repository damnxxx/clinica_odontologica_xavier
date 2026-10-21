# Skill: PostgreSQL e SQL explícito

Use esta skill ao criar ou alterar persistência.

1. Consulte o esquema atual e as migrações existentes antes de escrever SQL.
2. Crie migrações numeradas, incrementais e revisáveis; não altere migrações já aplicadas.
3. Prefira constraints de banco para invariantes estruturais (NOT NULL, UNIQUE, FK, CHECK).
4. Use parâmetros nomeados/posicionais via Spring JDBC; não concatene entradas em SQL.
5. Planeje transações para operações que alteram múltiplas tabelas.
6. Adicione índices conforme consultas reais e verifique planos quando necessário.
7. Escreva testes de integração usando PostgreSQL ou ambiente compatível; não confie apenas em H2 para validar diferenças de dialeto.
8. Defina comportamento para concorrência, exclusão e atualização.
9. Não grave segredos ou dados pessoais/clínicos em logs.
10. Documente rollback e riscos de migração.
