package br.com.clinica.odontologica.service;

import br.com.clinica.odontologica.dto.ClienteRequest;
import br.com.clinica.odontologica.dto.EnderecoRequest;
import br.com.clinica.odontologica.exception.RegraNegocioException;
import br.com.clinica.odontologica.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ClienteServiceIntegrationTest {
    @Autowired private ClienteService service;
    @Autowired private ClienteRepository repository;
    @Autowired private JdbcTemplate jdbc;

    @BeforeEach
    void limparBanco() {
        jdbc.update("DELETE FROM enderecos");
        jdbc.update("DELETE FROM clientes");
    }

    @Test
    void cadastraEConsultaClienteComEnderecoPersistidoPorSql() {
        var criado = service.cadastrar(request("12345678901", List.of(endereco("Rua Um"))));

        assertNotNull(criado.id());
        assertEquals(1, criado.enderecos().size());
        assertNotNull(criado.enderecos().get(0).id());

        var consultado = service.consultar(criado.id());
        assertEquals("Pessoa Teste", consultado.nome());
        assertEquals("Rua Um", consultado.enderecos().get(0).logradouro());
        assertEquals(1, service.listar().size());
    }

    @Test
    void rejeitaCpfDuplicado() {
        service.cadastrar(request("12345678901", List.of(endereco("Rua Um"))));

        assertThrows(RegraNegocioException.class,
                () -> service.cadastrar(request("12345678901", List.of(endereco("Rua Dois")))));
    }

    @Test
    void naoPermiteExcluirUltimoEndereco() {
        var cliente = service.cadastrar(request("12345678901", List.of(endereco("Rua Um"))));

        assertThrows(RegraNegocioException.class,
                () -> service.excluirEndereco(cliente.id(), cliente.enderecos().get(0).id()));
    }

    @Test
    void retornaVazioParaClienteInexistente() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
    }

    private ClienteRequest request(String cpf, List<EnderecoRequest> enderecos) {
        return new ClienteRequest("Pessoa Teste", cpf, "teste@example.com", "11999990000",
                null, null, enderecos);
    }

    private EnderecoRequest endereco(String logradouro) {
        return new EnderecoRequest(logradouro, "10", null, "Centro", "São Paulo", "SP", "01000-000");
    }
}
