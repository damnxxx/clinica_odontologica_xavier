package br.com.clinica.odontologica.controller;

import br.com.clinica.odontologica.repository.UsuarioRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private UsuarioRepository usuarios;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararUsuario() {
        jdbc.update("DELETE FROM usuarios");
        usuarios.inserir("Admin de Teste", "admin@example.com", passwordEncoder.encode("senha-super-segura"));
    }

    @Test
    void loginRetornaTokenEPermiteAcessoAutenticado() throws Exception {
        MvcResult login = mvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"email":"admin@example.com","senha":"senha-super-segura"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(1800))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        String token = JsonPath.read(login.getResponse().getContentAsString(), "$.accessToken");
        mvc.perform(get("/api/clientes").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void endpointsDaApiExigemToken() throws Exception {
        mvc.perform(get("/api/clientes")).andExpect(status().isUnauthorized());
    }

    @Test
    void credenciaisInvalidasNaoAutenticam() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"email":"admin@example.com","senha":"senha-errada"}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
