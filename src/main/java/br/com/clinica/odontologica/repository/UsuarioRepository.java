package br.com.clinica.odontologica.repository;
import br.com.clinica.odontologica.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public class UsuarioRepository {
    private final JdbcTemplate jdbc;
    public UsuarioRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM usuarios WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }
    public Optional<Usuario> findByEmail(String email) {
        List<Usuario> usuarios = jdbc.query("SELECT id, nome, email, senha_hash, ativo FROM usuarios WHERE email = ?",
                (rs, rowNum) -> new Usuario(rs.getObject("id", UUID.class), rs.getString("nome"),
                        rs.getString("email"), rs.getString("senha_hash"), rs.getBoolean("ativo")), email);
        return usuarios.stream().findFirst();
    }
    public void inserir(String nome, String email, String senhaHash) {
        jdbc.update("INSERT INTO usuarios (id, nome, email, senha_hash, ativo) VALUES (?, ?, ?, ?, TRUE)",
                UUID.randomUUID(), nome, email, senhaHash);
    }
}
