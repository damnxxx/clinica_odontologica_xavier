# Memória do projeto

## Estado conhecido
- Projeto Java 21 / Spring Boot 3.5.x / Maven.
- A versão inicial contém API CRUD de clientes e endereços.
- A persistência inicial usa Spring Data JPA e H2.
- Não assumir que autenticação, frontend, agenda ou prontuário já estejam implementados.

## Direção aprovada
- Evoluir em branch separada e integrar por pull request.
- Alvo: PostgreSQL, SQL explícito com Spring JDBC, JWT/Spring Security e frontend HTML/CSS/JavaScript.
- Construir e validar a IA Factory no próprio repositório, inspirada no fluxo da Clinicahans.

## Regras de colaboração
- Ler história, requisitos não funcionais, arquitetura e ADRs antes de mudanças.
- Manter o sistema compilável por etapa.
- Documentar decisões e lacunas; atualizar esta memória quando a estrutura mudar.
- Nunca incluir segredos nem dados reais de pacientes.
