package br.com.clinica.odontologica.service;
import br.com.clinica.odontologica.dto.LoginRequest;
import br.com.clinica.odontologica.dto.LoginResponse;
import br.com.clinica.odontologica.exception.CredenciaisInvalidasException;
import br.com.clinica.odontologica.model.Usuario;
import br.com.clinica.odontologica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
@Service
public class AuthService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final long ttlSeconds;
    private final String issuer;
    private final String audience;
    public AuthService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder,
                       @Value("${app.security.jwt-ttl-seconds:1800}") long ttlSeconds,
                       @Value("${app.security.jwt-issuer:clinica-odontologica}") String issuer,
                       @Value("${app.security.jwt-audience:clinica-odontologica-web}") String audience) {
        this.usuarios = usuarios; this.passwordEncoder = passwordEncoder; this.jwtEncoder = jwtEncoder;
        this.ttlSeconds = ttlSeconds; this.issuer = issuer; this.audience = audience;
    }
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarios.findByEmail(email).filter(Usuario::ativo)
                .filter(u -> passwordEncoder.matches(request.senha(), u.senhaHash()))
                .orElseThrow(CredenciaisInvalidasException::new);
        Instant agora = Instant.now();
        Instant expiraEm = agora.plusSeconds(ttlSeconds);
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).audience(List.of(audience))
                .subject(usuario.email()).issuedAt(agora).expiresAt(expiraEm)
                .claim("email", usuario.email()).claim("name", usuario.nome()).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                org.springframework.security.oauth2.jwt.JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", ttlSeconds, usuario.email());
    }
}
