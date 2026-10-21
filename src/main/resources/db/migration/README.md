# Migrações de banco

As migrações PostgreSQL serão adicionadas aqui como arquivos SQL versionados (por exemplo, `V1__schema_inicial.sql`). Não altere migrações que já tenham sido aplicadas em um ambiente compartilhado; crie uma nova migração corretiva.

**Estado desta etapa:** infraestrutura e convenção documentadas. A migração do esquema atual de clientes/endereços e a substituição dos repositórios JPA ainda precisam ser implementadas antes de apontar a aplicação para PostgreSQL.
