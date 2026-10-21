# Migrações de banco

As migrações executáveis do PostgreSQL ficam nesta pasta com nomes versionados pelo Flyway, no formato `V1__descricao.sql`.

- A primeira migração executável é `V1__schema_inicial.sql`.
- Não altere uma migração já aplicada em ambiente compartilhado; crie uma nova migração corretiva.
- A aplicação executa as migrações automaticamente na inicialização, antes de atender requisições.
