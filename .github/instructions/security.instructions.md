---
applyTo: "**/*"
---

# Segurança e privacidade

- Use HTTPS em produção e segredos via ambiente/gerenciador de segredos.
- JWT deve ter validação de assinatura, emissor/audiência quando configurados, expiração curta e política explícita de renovação/revogação.
- Nunca coloque senhas, tokens, CPF ou dados clínicos em logs.
- Faça autorização por recurso e papel no backend; não aceite o ID do usuário como prova de acesso.
- Use SQL parametrizado, validação de entrada e respostas de erro sem stack trace.
- Trate prontuários e informações de saúde como dados sensíveis; limite acesso e registre eventos de auditoria apropriados.
- Não use dados reais de pacientes em fixtures, screenshots ou documentação.
- Exija testes para casos de acesso permitido e negado.
