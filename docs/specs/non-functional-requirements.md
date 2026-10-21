# Requisitos não funcionais

## Segurança
- Autenticação para endpoints privados e autorização por papel e recurso.
- JWT validado no backend; segredos configurados fora do Git.
- SQL parametrizado, validação de entrada e tratamento seguro de erros.
- Princípio do menor privilégio e testes para acesso negado.

## Privacidade e auditoria
- Dados pessoais e de saúde não devem aparecer em logs, fixtures ou exemplos reais.
- Acesso a dados clínicos deve ser restrito e auditável.
- Alterações em registros clínicos finalizados precisam preservar histórico e autoria.

## Qualidade e operação
- Java 21, build Maven e testes automatizados.
- PostgreSQL como banco-alvo, com migrações SQL versionadas.
- Configuração por variáveis de ambiente; nenhuma credencial padrão em produção.
- Logs estruturados sem conteúdo sensível, health checks e documentação de execução.
- Frontend responsivo e acessível, com estados de carregamento, vazio e erro.
