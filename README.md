# Clínica Odontológica Xavier

API REST para cadastro e manutenção de clientes e seus endereços, organizada em camadas (`controller`, `service`, `repository`, `model` e `dto`). O relacionamento é relacional: um cliente possui um ou mais endereços; cada endereço pertence a um único cliente.

## Requisitos e execução

- Java 21 e Maven 3.9+
- Execute na raiz: `mvn spring-boot:run`
- A API inicia em `http://localhost:8080`.
- Banco H2 em arquivo `./data/clinica_uuid_cliente`, que mantém os dados entre reinicializações. Console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/clinica_uuid_cliente`, usuário `sa`, sem senha).
- Clientes usam identificadores UUID e endereços usam IDs numéricos, gerados automaticamente e retornados nas respostas da API.

## Operações

| Método | Rota | Ação |
| --- | --- | --- |
| POST | `/api/clientes` | Cadastra cliente com pelo menos um endereço |
| GET | `/api/clientes` | Lista clientes com seus endereços |
| GET | `/api/clientes/{id}` | Consulta um cliente |
| PUT | `/api/clientes/{id}` | Atualiza dados e a lista de endereços do cliente |
| DELETE | `/api/clientes/{id}` | Exclui cliente e seus endereços |
| POST | `/api/clientes/{id}/enderecos` | Adiciona outro endereço |
| PUT | `/api/clientes/{id}/enderecos/{enderecoId}` | Altera um endereço |
| DELETE | `/api/clientes/{id}/enderecos/{enderecoId}` | Exclui endereço, desde que reste ao menos um |

Exemplo de `POST /api/clientes` (datas no formato `AAAA-MM-DD`):

```json
{
  "nome": "Maria da Silva",
  "cpf": "123.456.789-00",
  "email": "maria@example.com",
  "telefone": "11999998888",
  "dataNascimento": "1990-05-10",
  "observacoesClinicas": "Alergia a penicilina",
  "enderecos": [
    {
      "logradouro": "Rua das Flores",
      "numero": "123",
      "complemento": "Apto 42",
      "bairro": "Centro",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01000-000"
    }
  ]
}
```

CPF é armazenado sem pontuação e deve ser único. Erros de validação retornam HTTP 400, recursos ausentes 404 e conflitos de regra (CPF repetido ou tentativa de remover o único endereço) 409.

## Próximos módulos sugeridos

Depois do cadastro básico, modele `Dentista` (CRO, especialidade, contato), `Atendente` (contato e função) e `Consulta` (cliente, dentista, data/hora, procedimento, status e observações). A consulta liga cliente e dentista e permite evoluir para agenda e prontuário; dados clínicos devem ter acesso controlado.
