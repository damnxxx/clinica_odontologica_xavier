package br.com.clinica.odontologica.config;
import br.com.clinica.odontologica.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Locale;
@Component
public class AdminBootstrap implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final String email, nome, senha;
    public AdminBootstrap(UsuarioRepository usuarios, PasswordEncoder passwordEncoder,
                          @Value("${app.bootstrap-admin.email:}") String email,
                          @Value("${app.bootstrap-admin.name:Administrador}") String nome,
                          @Value("${app.bootstrap-admin.password:}") String senha) {
        this.usuarios = usuarios; this.passwordEncoder = passwordEncoder;
        this.email = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        this.nome = nome == null || nome.isBlank() ? "Administrador" : nome.trim();
        this.senha = senha == null ? "" : senha;
    }
    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() && senha.isBlank()) {
            log.info("Bootstrap de administrador ignorado; configure ADMIN_EMAIL e ADMIN_PASSWORD para criar o primeiro acesso.");
            return;
        }
        if (email.isBlank() || senha.isBlank()) throw new IllegalStateException("Configure ADMIN_EMAIL e ADMIN_PASSWORD juntos para criar o primeiro acesso.");
        if (senha.length() < 12) throw new IllegalStateException("ADMIN_PASSWORD deve ter pelo menos 12 caracteres.");
        if (!usuarios.existsByEmail(email)) {
            usuarios.inserir(nome, email, passwordEncoder.encode(senha));
            log.info("Conta administrativa inicial criada a partir das variáveis de ambiente.");
        }
    }
}
