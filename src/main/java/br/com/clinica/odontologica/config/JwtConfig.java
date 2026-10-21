package br.com.clinica.odontologica.config;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
@Configuration
public class JwtConfig {
    @Bean
    SecretKey jwtSecretKey(@Value("${app.security.jwt-secret:}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) throw new IllegalStateException("JWT_SECRET deve conter pelo menos 32 bytes. Configure-o no ambiente ou no arquivo .env local.");
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtSecretKey) { return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey)); }
    @Bean
    JwtDecoder jwtDecoder(SecretKey jwtSecretKey,
                          @Value("${app.security.jwt-issuer:clinica-odontologica}") String issuer,
                          @Value("${app.security.jwt-audience:clinica-odontologica-web}") String audience) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> jwt.getAudience().contains(audience)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Token audience is invalid", null));
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer), audienceValidator));
        return decoder;
    }
}
