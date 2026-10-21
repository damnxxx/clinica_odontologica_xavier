package br.com.clinica.odontologica.repository;

import br.com.clinica.odontologica.model.Cliente;
import br.com.clinica.odontologica.model.Endereco;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClienteRepository {
    private static final String SELECT_CLIENTE = """
            SELECT id, nome, cpf, email, telefone, data_nascimento, observacoes_clinicas
            FROM clientes
            """;
    private static final String SELECT_ENDERECOS = """
            SELECT id, logradouro, numero, complemento, bairro, cidade, estado, cep
            FROM enderecos WHERE cliente_id = ? ORDER BY id
            """;

    private final JdbcTemplate jdbc;

    private final RowMapper<Cliente> clienteMapper = (rs, rowNum) -> {
        Cliente cliente = new Cliente(
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("email"),
                rs.getString("telefone"),
                rs.getDate("data_nascimento") == null ? null : rs.getDate("data_nascimento").toLocalDate(),
                rs.getString("observacoes_clinicas"));
        cliente.setId(rs.getObject("id", UUID.class));
        return cliente;
    };

    private final RowMapper<Endereco> enderecoMapper = (rs, rowNum) -> {
        Endereco endereco = new Endereco(
                rs.getString("logradouro"), rs.getString("numero"), rs.getString("complemento"),
                rs.getString("bairro"), rs.getString("cidade"), rs.getString("estado"), rs.getString("cep"));
        endereco.setId(rs.getLong("id"));
        return endereco;
    };

    public ClienteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsByCpf(String cpf) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM clientes WHERE cpf = ?", Integer.class, cpf);
        return count != null && count > 0;
    }

    public boolean existsByCpfAndIdNot(String cpf, UUID id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM clientes WHERE cpf = ? AND id <> ?", Integer.class, cpf, id);
        return count != null && count > 0;
    }

    public Cliente inserir(Cliente cliente) {
        if (cliente.getId() == null) cliente.setId(UUID.randomUUID());
        jdbc.update("""
                INSERT INTO clientes (id, nome, cpf, email, telefone, data_nascimento, observacoes_clinicas)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, cliente.getId(), cliente.getNome(), cliente.getCpf(), cliente.getEmail(), cliente.getTelefone(),
                cliente.getDataNascimento() == null ? null : Date.valueOf(cliente.getDataNascimento()),
                cliente.getObservacoesClinicas());
        for (Endereco endereco : cliente.getEnderecos()) inserirEndereco(cliente.getId(), endereco);
        return cliente;
    }

    public Cliente atualizar(Cliente cliente) {
        jdbc.update("""
                UPDATE clientes SET nome = ?, cpf = ?, email = ?, telefone = ?, data_nascimento = ?, observacoes_clinicas = ?
                WHERE id = ?
                """, cliente.getNome(), cliente.getCpf(), cliente.getEmail(), cliente.getTelefone(),
                cliente.getDataNascimento() == null ? null : Date.valueOf(cliente.getDataNascimento()),
                cliente.getObservacoesClinicas(), cliente.getId());
        jdbc.update("DELETE FROM enderecos WHERE cliente_id = ?", cliente.getId());
        for (Endereco endereco : cliente.getEnderecos()) inserirEndereco(cliente.getId(), endereco);
        return cliente;
    }

    public List<Cliente> findAll() {
        List<Cliente> clientes = jdbc.query(SELECT_CLIENTE + " ORDER BY nome, id", clienteMapper);
        clientes.forEach(this::carregarEnderecos);
        return clientes;
    }

    public Optional<Cliente> findById(UUID id) {
        List<Cliente> encontrados = jdbc.query(SELECT_CLIENTE + " WHERE id = ?", clienteMapper, id);
        if (encontrados.isEmpty()) return Optional.empty();
        Cliente cliente = encontrados.get(0);
        carregarEnderecos(cliente);
        return Optional.of(cliente);
    }

    public void excluir(UUID id) {
        jdbc.update("DELETE FROM clientes WHERE id = ?", id);
    }

    public Endereco inserirEndereco(UUID clienteId, Endereco endereco) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO enderecos (cliente_id, logradouro, numero, complemento, bairro, cidade, estado, cep)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            statement.setObject(1, clienteId);
            statement.setString(2, endereco.getLogradouro());
            statement.setString(3, endereco.getNumero());
            statement.setString(4, endereco.getComplemento());
            statement.setString(5, endereco.getBairro());
            statement.setString(6, endereco.getCidade());
            statement.setString(7, endereco.getEstado());
            statement.setString(8, endereco.getCep());
            return statement;
        }, keys);
        Number key = keys.getKey();
        if (key == null) throw new IllegalStateException("O banco não retornou o ID do endereço inserido.");
        endereco.setId(key.longValue());
        return endereco;
    }

    public boolean atualizarEndereco(UUID clienteId, Endereco endereco) {
        int atualizados = jdbc.update("""
                UPDATE enderecos SET logradouro = ?, numero = ?, complemento = ?, bairro = ?, cidade = ?, estado = ?, cep = ?
                WHERE id = ? AND cliente_id = ?
                """, endereco.getLogradouro(), endereco.getNumero(), endereco.getComplemento(), endereco.getBairro(),
                endereco.getCidade(), endereco.getEstado(), endereco.getCep(), endereco.getId(), clienteId);
        return atualizados == 1;
    }

    public void excluirEndereco(UUID clienteId, Long enderecoId) {
        jdbc.update("DELETE FROM enderecos WHERE cliente_id = ? AND id = ?", clienteId, enderecoId);
    }

    private void carregarEnderecos(Cliente cliente) {
        List<Endereco> enderecos = jdbc.query(SELECT_ENDERECOS, enderecoMapper, cliente.getId());
        enderecos.forEach(cliente::adicionarEndereco);
    }
}
